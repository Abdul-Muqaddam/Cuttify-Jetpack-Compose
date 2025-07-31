package com.example.videotoaudioconverter.presentation.home_screen.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.kaaveh.sdpcompose.sdp

@Composable
fun HorizontalSpacer(width: Int) {
    Spacer(modifier = Modifier.width(width.sdp))
}