package com.example.videotoaudioconverter.navigation

import androidx.navigation.NavController

class AppDestination(private val navController: NavController) {


    fun navigateToEachVideoPreviewAndPlayerScreen(videoUri: String,videoTitle: String){
        navController.navigate(Routes.EachVideoPreviewAndPlayerRoute(videoUri,videoTitle))
    }
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
    fun navigateToLanguageScreen(){
        navController.navigate(Routes.LanguageScreenRoute)
    }
    fun navigateToFeedbackScreen(){
        navController.navigate(Routes.FeedbackScreenRoute)
    }
    fun navigateToSetRingtoneScreen(){
        navController.navigate(Routes.SetRingtoneScreenRoute)
    }
    fun navigateToRateUsScreen(){
        navController.navigate(Routes.RateUsScreenRoute)
    }

    fun navigateToSuccessScreen(fileName:String,filePath:String){
        navController.navigate(Routes.SuccessScreenRoute(fileName,filePath))
    }
    fun navigateBack(){
        navController.popBackStack()
    }
}