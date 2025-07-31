package com.example.videotoaudioconverter.presentation.all_video_files.components

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val bitmap = retriever.frameAtTime
        retriever.release()
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}