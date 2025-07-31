package com.example.videotoaudioconverter.presentation.select_video_all_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.videotoaudioconverter.R
import ir.kaaveh.sdpcompose.ssp

@Composable
fun SeletVideoAllScreen() {
    Column {
        Row {
            Image(painter = painterResource(R.drawable.ic_back_arrow),
            contentDescription = null)
            Text(text = stringResource(R.string.select_video),
                fontSize = 26.ssp
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(painter = painterResource(R.drawable.ic_search),
                contentDescription = null)
        }
    }
}