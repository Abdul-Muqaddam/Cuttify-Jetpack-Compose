package com.example.videotoaudioconverter.presentation.all_folder

import android.content.Context
import android.provider.MediaStore
import java.io.File


data class VideoFolder(
    val name: String,
    val path: String,
    val dateAdded: Long = 0L,
    val size: Long = 0L
)

fun getAllVideoFolders(context: Context): List<VideoFolder> {
    val folderSet = mutableSetOf<VideoFolder>()
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
            val parentFile = File(videoPath).parentFile
            if (parentFile != null) {
                val folderName = parentFile.name
                val folderPath = parentFile.absolutePath
                folderSet.add(VideoFolder(folderName, folderPath))
            }
        }
    }

    return folderSet.toList()
}
