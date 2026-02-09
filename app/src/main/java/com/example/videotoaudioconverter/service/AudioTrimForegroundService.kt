package com.example.videotoaudioconverter.service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.audio_cutter_screen.AudioTrimCallback
import kotlinx.coroutines.*

class AudioTrimForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "audio_trim_channel"
        const val NOTIF_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val inputPath = intent?.getStringExtra("inputPath")
        val startMs = intent?.getLongExtra("startMs", 0L) ?: 0L
        val endMs = intent?.getLongExtra("endMs", 0L) ?: 0L
        val outputPath = intent?.getStringExtra("outputPath")

        if (inputPath.isNullOrEmpty() || outputPath.isNullOrEmpty()) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Start foreground notification
        startForeground(NOTIF_ID, createNotification("Trimming audio...", 0))

        serviceScope.launch {
            try {
                trimAudio(inputPath, startMs, endMs, outputPath)
                AudioTrimCallback.viewModel?.onAudioTrimCompleted(outputPath)
            } catch (e: Exception) {
                Log.e("AudioTrimService", "Error trimming audio", e)
                AudioTrimCallback.viewModel?.onAudioTrimFailed(e.message ?: "Unknown error")
            } finally {
                stopForeground(true)
                stopSelf()
            }
        }

        return START_STICKY
    }

    private suspend fun trimAudio(input: String, startMs: Long, endMs: Long, output: String) {
        withContext(Dispatchers.IO) {
            val durationSec = (endMs - startMs) / 1000.0
            val cmd = "-i \"$input\" -ss ${startMs / 1000.0} -to ${endMs / 1000.0} -c copy \"$output\""

            val session = FFmpegKit.execute(cmd)

            if (ReturnCode.isSuccess(session.returnCode)) {
                Log.d("AudioTrimService", "Audio trimmed successfully: $output")
            } else {
                throw Exception("FFmpeg failed: ${session.returnCode} / ${session.failStackTrace}")
            }
        }
    }

    private fun createNotification(content: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Audio Trimming")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_audio)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Trim",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of audio trimming"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}