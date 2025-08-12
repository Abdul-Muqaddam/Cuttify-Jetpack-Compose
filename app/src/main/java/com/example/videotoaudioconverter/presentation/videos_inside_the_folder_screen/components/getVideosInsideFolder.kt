package com.example.videotoaudioconverter.presentation.videos_inside_the_folder_screen.components

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

fun getVideosInFolder(context: Context, folderPath: String): List<Uri> {
    val videoList = mutableListOf<Uri>()

    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DATA
    )

    val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

    // Filter to only videos in the folder
    val selection = "${MediaStore.Video.Media.DATA} LIKE ?"
    val selectionArgs = arrayOf("%$folderPath%")

    val query = context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
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
