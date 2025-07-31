package com.example.videotoaudioconverter.presentation.all_video_files.components

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

fun getVideoDuration(context: Context, videoUri: Uri): String {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, videoUri)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationMs = durationStr?.toLongOrNull() ?: 0L
        val minutes = durationMs / 1000 / 60
        val seconds = (durationMs / 1000) % 60
        String.format("%02d:%02d", minutes, seconds)
    } catch (e: Exception) {
        "00:00"
    } finally {
        retriever.release()
    }
}