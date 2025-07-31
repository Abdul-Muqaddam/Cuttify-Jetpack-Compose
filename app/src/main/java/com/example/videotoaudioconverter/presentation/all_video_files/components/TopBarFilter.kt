package com.example.videotoaudioconverter.presentation.all_video_files.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun TopBarFilter(totalItems:Int){
    Row(
        modifier = Modifier
            .padding(vertical = 10.sdp, horizontal = 16.sdp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$totalItems items", color = MyColors.Green058, fontSize = 18.ssp)
        Image(
            modifier = Modifier.size(22.sdp),
            painter = painterResource(R.drawable.ic_filter),
            contentDescription = null
        )
    }
}



