package com.example.videotoaudioconverter.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationUtil {
    const val CHANNEL_ID ="video_to_audio_channel"

    fun createChannel(context: Context){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel=NotificationChannel(
                CHANNEL_ID,
                "Video to Audio Conversion",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager =context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}