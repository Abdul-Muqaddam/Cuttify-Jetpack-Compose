package com.example.videotoaudioconverter.presentation.all_folder.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun FolderComponentUi(folderPath:String, folderName: String,navigateToFolderVideos:(String)-> Unit) {
    Row(
        modifier = Modifier
            .padding(10.sdp)
            .background(Color.White).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }){
                navigateToFolderVideos(folderPath)
            },
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