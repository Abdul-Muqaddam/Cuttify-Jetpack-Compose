package com.example.videotoaudioconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.videotoaudioconverter.presentation.splash_screen.SplashScreen
import com.example.videotoaudioconverter.ui.theme.VideoToAudioConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideoToAudioConverterTheme {
                SplashScreen()
            }
        }
    }
}

