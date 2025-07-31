package com.example.videotoaudioconverter.navigation

import androidx.navigation.NavController

class AppDestination(private val navController: NavController) {
    fun navigateToSettingsScreen() {
        navController.navigate(Routes.SettingScreenRoute)
    }
    fun navigateToMainScreen(){
        navController.navigate(Routes.MainScreenRoute) {
            popUpTo(Routes.SplashScreenRoute) { inclusive = true }
        }
    }
    fun navigateToVideoToAudioConverterScreen(){
        navController.navigate(Routes.VideoToAudioConverterRoute)
    }
    fun navigateBack(){
        navController.popBackStack()
    }
}