package com.example.videotoaudioconverter.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.videotoaudioconverter.R

class AudioMergeService : Service() {

    companion object {

        const val CHANNEL_ID = "audio_merge_channel"
        const val NOTIFICATION_ID = 202

        var instance: AudioMergeService? = null

    }

    override fun onBind(intent: android.content.Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(0))
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    fun updateProgress(progress: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(progress))
    }

    fun showSuccess() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildSuccessNotification())
        stopSelf()
    }

    private fun buildNotification(progress: Int) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Cuttify — Merging Audio")
            .setContentText("Merging... $progress%")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setSilent(true)
            .build()

    private fun buildSuccessNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Cuttify — Audio Saved!")
            .setContentText("Your merged audio has been saved successfully")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Merging",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}