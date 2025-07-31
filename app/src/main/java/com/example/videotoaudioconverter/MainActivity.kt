package com.example.videotoaudioconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.videotoaudioconverter.navigation.AppNavHost
import com.example.videotoaudioconverter.presentation.setting_screen.SettingScreen
import com.example.videotoaudioconverter.presentation.home_screen.HomeScreen
import com.example.videotoaudioconverter.presentation.main_screen.MainScreen
import com.example.videotoaudioconverter.presentation.splash_screen.SplashScreen
import com.example.videotoaudioconverter.ui.theme.VideoToAudioConverterTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideoToAudioConverterTheme {
                AppNavHost()
            }
        }
    }
}

