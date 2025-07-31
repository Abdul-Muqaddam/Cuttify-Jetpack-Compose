package com.example.videotoaudioconverter.presentation.home_screen.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.ssp

@Composable
fun MainText(text: String) {
    Text(text = text, fontWeight = FontWeight.Bold, fontSize = 20.ssp, color = MyColors.Green058)
}