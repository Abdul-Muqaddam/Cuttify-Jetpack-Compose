package com.example.videotoaudioconverter.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.Utils.TrimEventBus
import com.example.videotoaudioconverter.presentation.Utils.VideoTrimmerUtil

class VideoTrimmerService : Service() {

    companion object {
        const val ACTION_START_TRIM = "ACTION_START_TRIM"
        const val EXTRA_VIDEO_URI = "EXTRA_VIDEO_URI"
        const val EXTRA_START_MS = "EXTRA_START_MS"
        const val EXTRA_END_MS = "EXTRA_END_MS"

        const val CHANNEL_ID = "video_trim_channel"
        const val NOTIFICATION_ID = 101

        // Broadcast actions to update ViewModel
        const val BROADCAST_PROGRESS = "com.example.videotoaudioconverter.TRIM_PROGRESS"
        const val BROADCAST_SUCCESS = "com.example.videotoaudioconverter.TRIM_SUCCESS"
        const val BROADCAST_ERROR = "com.example.videotoaudioconverter.TRIM_ERROR"
        const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        const val EXTRA_FILE_PATH = "EXTRA_FILE_PATH"
        const val EXTRA_ERROR_MSG = "EXTRA_ERROR_MSG"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START_TRIM) {
            val uriString = intent.getStringExtra(EXTRA_VIDEO_URI) ?: return START_NOT_STICKY
            val startMs = intent.getLongExtra(EXTRA_START_MS, 0L)
            val endMs = intent.getLongExtra(EXTRA_END_MS, 0L)
            val uri = Uri.parse(uriString)

            // Show initial notification
            startForeground(NOTIFICATION_ID, buildNotification(0))

            // Start trimming
            VideoTrimmerUtil.trimVideo(
                context = this,
                inputUri = uri,
                startMs = startMs,
                endMs = endMs,
                onProgress = { progress ->
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.notify(NOTIFICATION_ID, buildNotification(progress))
                    TrimEventBus.sendProgress(progress)
                },
                onSuccess = { file ->
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.notify(NOTIFICATION_ID, buildSuccessNotification())
                    TrimEventBus.sendSuccess(file.absolutePath)
                    stopSelf()
                },
                onError = { message ->
                    TrimEventBus.sendError(message)
                    stopSelf()
                }
            )
        }
        return START_NOT_STICKY
    }

    private fun buildNotification(progress: Int) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Cuttify — Trimming Video")
            .setContentText("Saving your trimmed video... $progress%")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setSilent(true)
            .build()

    private fun buildSuccessNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Cuttify — Video Saved!")
            .setContentText("Your trimmed video has been saved to Movies/Cuttify/Trimmed Video")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Video Trimming",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows video trimming progress"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}