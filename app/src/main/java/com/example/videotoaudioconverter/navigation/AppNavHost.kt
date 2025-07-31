package com.example.videotoaudioconverter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.videotoaudioconverter.presentation.main_screen.MainScreen
import com.example.videotoaudioconverter.presentation.main_screen.permission.VideoAndPhotoPermission
import com.example.videotoaudioconverter.presentation.setting_screen.SettingScreen
import com.example.videotoaudioconverter.presentation.splash_screen.SplashScreen
import com.example.videotoaudioconverter.presentation.video_to_audio_converter.VideoToAudioConverterScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val appDestination = remember (navController){ AppDestination(navController) }
    NavHost(
        navController = navController,
        startDestination = Routes.SplashScreenRoute


    ) {
        composable<Routes.MainScreenRoute> {
            MainScreen(navigateToSettingScreen = {
                appDestination.navigateToSettingsScreen()
            },navigateToVideToAudioConverter={
                appDestination.navigateToVideoToAudioConverterScreen()
            })
        }
        composable<Routes.VideoToAudioConverterRoute> {
            VideoToAudioConverterScreen(
                navigateBack = {
                    appDestination.navigateBack()
                }
            )
        }
        composable<Routes.SplashScreenRoute> {
            SplashScreen(navigateToHome = {
                appDestination.navigateToMainScreen()
            })
        }


        composable<Routes.SettingScreenRoute> {
            SettingScreen()
        }

    }
}