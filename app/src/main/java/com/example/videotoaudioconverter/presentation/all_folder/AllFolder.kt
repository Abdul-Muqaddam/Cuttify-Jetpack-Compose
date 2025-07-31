package com.example.videotoaudioconverter.presentation.all_folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.all_video_files.components.TopBarFilter
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun AllFolder() {
    val context = LocalContext.current
    val folders = remember { getAllVideoFolders(context) }

    Column {
        TopBarFilter(folders.size)
        LazyColumn {
            items(folders) { folderName ->
                FolderComponentUi(folderName = folderName)
            }
        }

    }
}


@Composable
fun FolderComponentUi(folderName: String) {
    Row(
        modifier = Modifier
            .padding(10.sdp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Image(
            contentDescription = null,
            painter = painterResource(R.drawable.ic_folder),
            modifier = Modifier
                .size(40.sdp)
        )

        Text(
            text = folderName,
            modifier = Modifier
                .padding(start = 10.sdp)
                .weight(1f),
            color = MyColors.Green058,
            fontSize = 16.ssp
        )

        Image(
            contentDescription = null,
            painter = painterResource(R.drawable.ic_right),
            modifier = Modifier
                .padding(start = 10.sdp)
                .size(20.sdp),
        )
    }
}