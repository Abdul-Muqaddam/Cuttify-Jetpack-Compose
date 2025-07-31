package com.example.videotoaudioconverter.presentation.all_folder

import android.content.Context
import android.provider.MediaStore
import java.io.File

fun getAllVideoFolders(context: Context): List<String> {
    val folderSet = mutableSetOf<String>()
    val projection = arrayOf(
        MediaStore.Video.Media.DATA
    )

    val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val cursor = context.contentResolver.query(
        uri,
        projection,
        null,
        null,
        null
    )

    cursor?.use {
        val dataIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

        while (it.moveToNext()) {
            val videoPath = it.getString(dataIndex)
            val folderPath = File(videoPath).parentFile?.name
            if (folderPath != null) {
                folderSet.add(folderPath)
            }
        }
    }

    return folderSet.toList()
}
