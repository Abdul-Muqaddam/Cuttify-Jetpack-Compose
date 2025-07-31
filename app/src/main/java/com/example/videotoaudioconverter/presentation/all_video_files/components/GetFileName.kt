package com.example.videotoaudioconverter.presentation.all_video_files.components

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun getFileName(context: Context, uri: Uri): String {
    var name = "Unknown"
    val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        if (cursor.moveToFirst()) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}