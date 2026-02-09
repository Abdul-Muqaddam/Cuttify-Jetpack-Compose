package com.example.videotoaudioconverter.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.videotoaudioconverter.MainActivity
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.NotificationUtil
import com.example.videotoaudioconverter.presentation.NotificationUtil.CHANNEL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer


class VideoToAudioService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "video_to_audio_channel"
        const val ACTION_UPDATE_PROGRESS = "UPDATE_PROGRESS"
        const val EXTRA_PROGRESS = "PROGRESS"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // 🔹 Only update notification progress
        if (intent?.action == ACTION_UPDATE_PROGRESS) {
            val progress = intent.getIntExtra(EXTRA_PROGRESS, 0)
            updateNotification("Conversion in progress: $progress%")

            if (progress >= 100) {
                updateNotification("Conversion completed")
                stopForeground(false)
                stopSelf()
            }
            return START_STICKY
        }

        // 🔹 Start foreground service (NO conversion here)
        startForeground(
            NOTIFICATION_ID,
            buildNotification("Preparing conversion...")
        )

        return START_STICKY
    }

    private fun updateNotification(text: String) {
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    private fun buildNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Video to Audio Converter")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Video to Audio Conversion",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
//
//class VideoToAudioService  : Service() {
//
//
//    private val channelId = "audio_conversion_channel"
//
//    override fun onBind(intent: Intent?): IBinder? = null
//
//    override fun onCreate() {
//        super.onCreate()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Audio Conversion",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            val manager = getSystemService(NotificationManager::class.java)
//            manager?.createNotificationChannel(channel)
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val videoPath = intent?.getStringExtra("video_path") ?: return START_NOT_STICKY
//        val outputPath = intent.getStringExtra("output_path") ?: return START_NOT_STICKY
//
//        startForeground(1, buildNotification(0))
//
//        Thread {
//            convertVideoToAudio(videoPath, outputPath)
//        }.start()
//
//        return START_NOT_STICKY
//    }
//
//    private fun buildNotification(progress: Int): Notification {
//        val builder = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Converting video to audio")
//            .setSmallIcon(R.drawable.ic_conversion)
//            .setProgress(100, progress, false)
//            .setOngoing(true)
//        return builder.build()
//    }
//
//    private fun convertVideoToAudio(videoPath: String, outputPath: String) {
//        val extractor = MediaExtractor()
//        extractor.setDataSource(videoPath)
//        var audioTrackIndex = -1
//
//        for (i in 0 until extractor.trackCount) {
//            val format = extractor.getTrackFormat(i)
//            val mime = format.getString(MediaFormat.KEY_MIME)
//            if (mime?.startsWith("audio/") == true) {
//                audioTrackIndex = i
//                break
//            }
//        }
//
//        if (audioTrackIndex == -1) {
//            stopSelf()
//            return
//        }
//
//        extractor.selectTrack(audioTrackIndex)
//        val format = extractor.getTrackFormat(audioTrackIndex)
//        val duration = format.getLong(MediaFormat.KEY_DURATION) // in microseconds
//
//        val muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
//        val muxerAudioTrack = muxer.addTrack(format)
//        muxer.start()
//
//        val buffer = ByteBuffer.allocate(1024 * 1024)
//        val bufferInfo = MediaCodec.BufferInfo()
//        var totalRead: Long = 0
//
//        while (true) {
//            val sampleSize = extractor.readSampleData(buffer, 0)
//            if (sampleSize < 0) break
//
//            bufferInfo.offset = 0
//            bufferInfo.size = sampleSize
//            bufferInfo.presentationTimeUs = extractor.sampleTime
//
//            val sampleFlags = extractor.sampleFlags
//            bufferInfo.flags = if ((sampleFlags and MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
//                MediaCodec.BUFFER_FLAG_KEY_FRAME
//            } else 0
//
//
//            muxer.writeSampleData(muxerAudioTrack, buffer, bufferInfo)
//            extractor.advance()
//
//            totalRead = bufferInfo.presentationTimeUs
//
//            val progress = ((totalRead.toDouble() / duration) * 100).toInt().coerceIn(0, 100)
//            val notification = buildNotification(progress)
//            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.notify(1, notification)
//        }
//
//        muxer.stop()
//        muxer.release()
//        extractor.release()
//
//        stopForeground(true)
//        stopSelf()
//    }
//}


//class VideoToAudioService : Service(){
//    private val notificationId = 1
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        val videoUriString = intent?.getStringExtra("VIDEO_URI")
//        val videoTitle = intent?.getStringExtra("VIDEO_TITLE") ?: "audio"
//
//        startForeground(notificationId,createNotification(0))
//
//        Thread {
//            for (progress in 1..100) {
//                Thread.sleep(100)
//                updateNotification(progress)
//            }
//
//            stopForeground(true)
//            stopSelf()
//        }.start()
//
//        return START_NOT_STICKY
//    }
//    private fun createNotification(progress: Int): Notification {
//        return NotificationCompat.Builder(this, NotificationUtil.CHANNEL_ID)
//            .setContentTitle("Converting video to audio")
//            .setContentText("Progress: $progress%")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setProgress(100, progress, false)
//            .setOngoing(true)
//            .build()
//    }
//
//    private fun updateNotification(progress: Int) {
//        val manager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.notify(notificationId, createNotification(progress))
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//
//}