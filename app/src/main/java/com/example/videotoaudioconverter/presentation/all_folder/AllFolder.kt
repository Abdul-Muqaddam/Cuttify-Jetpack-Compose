package com.example.videotoaudioconverter.presentation.all_folder

import android.content.Context
import android.provider.MediaStore
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import java.io.File

@Composable
fun AllFolder() {
    val context = LocalContext.current
    val folders = remember { getAllVideoFolders(context) }

    LazyColumn {
        items(folders) { folderName ->
            Text(
                text = folderName,
                color = Color.Black,
                fontSize = 16.ssp,
                modifier = Modifier.padding(8.sdp)
            )
        }
    }
}


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
