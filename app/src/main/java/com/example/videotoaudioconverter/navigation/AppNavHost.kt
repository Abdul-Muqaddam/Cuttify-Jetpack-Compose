package com.example.videotoaudioconverter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.videotoaudioconverter.presentation.LanguageScreen.LanguageScreen
import com.example.videotoaudioconverter.presentation.each_video_preview_and_player_screen.EachVideoPreviewAndPlayerScreen
import com.example.videotoaudioconverter.presentation.main_screen.MainScreen
import com.example.videotoaudioconverter.presentation.setting_screen.SettingScreen
import com.example.videotoaudioconverter.presentation.splash_screen.SplashScreen
import com.example.videotoaudioconverter.presentation.video_to_audio_converter.VideoToAudioConverterScreen
import androidx.core.net.toUri
import androidx.navigation.toRoute
import com.example.videotoaudioconverter.presentation.success_screen.SuccessScreen

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
                videoClicked={videoUri,videoTitle->
                  appDestination.navigateToEachVideoPreviewAndPlayerScreen(videoUri = videoUri.toString(),videoTitle=videoTitle)
                },
                navigateBack = {
                    appDestination.navigateBack()
                }
            )
        }
        composable<Routes.EachVideoPreviewAndPlayerRoute> {
            val videoUri = it.toRoute<Routes.EachVideoPreviewAndPlayerRoute>()
            EachVideoPreviewAndPlayerScreen(videoUri = videoUri.videoUriString.toUri(), fileName =videoUri.videoTitle,navigateToBack={
                appDestination.navigateBack()
            } ,navigateToSuccessScreen={fileName,filePath->
                appDestination.navigateToSuccessScreen(fileName,filePath)
            })
        }
        composable<Routes.SplashScreenRoute> {
            SplashScreen(navigateToHome = {
                appDestination.navigateToMainScreen()
            })
        }

        composable<Routes.SuccessScreenRoute> {
            val routeData=it.toRoute<Routes.SuccessScreenRoute>()
            SuccessScreen(fileName=routeData.fileName, filePath = routeData.filePath)
        }

        composable<Routes.SettingScreenRoute> {
            SettingScreen(navigateToLanguageScreen = {
                appDestination.navigateToLanguageScreen()
            })
        }

        composable<Routes.LanguageScreenRoute> {
            LanguageScreen()
        }

    }
}