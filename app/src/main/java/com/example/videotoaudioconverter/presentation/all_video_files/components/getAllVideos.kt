package com.example.videotoaudioconverter.presentation.all_video_files.components

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

fun getAllVideos(context: Context): List<Uri> {
    val videoList = mutableListOf<Uri>()

    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Video.Media._ID
    )

    val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

    val query = context.contentResolver.query(
        collection,
        projection,
        null,
        null,
        sortOrder
    )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(collection, id)
            videoList.add(contentUri)
        }
    }

    return videoList
}
