package com.example.videotoaudioconverter.presentation.shorts_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ShortsScreen(){
    Text(modifier = Modifier.statusBarsPadding().fillMaxSize(), text = "this is Shorts Section")
}