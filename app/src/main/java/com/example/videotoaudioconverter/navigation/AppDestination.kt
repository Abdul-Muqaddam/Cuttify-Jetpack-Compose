package com.example.videotoaudioconverter.navigation

import android.net.Uri
import androidx.navigation.NavController

class AppDestination(private val navController: NavController) {


    fun navigateToVideoPlayerScreen(videoUri: String){
        navController.navigate(Routes.VideoPlayerRoute(videoUri))
    }
    fun navigateToAudioPlayerScreen(){
        navController.navigate(Routes.AudioPlayerRoute)
    }
    fun navigateToAllVideosOfFolderScreen(folderPath:String,fromWhichScreen: String){
        navController.navigate(Routes.VideosInsideTheFolderRoutes(folderPath= folderPath, fromWhichScreen = fromWhichScreen))
    }
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
    fun navigateToVideoToAudioConverterScreen(fromWhichScreen:String){
        navController.navigate(Routes.VideoToAudioConverterRoute(fromWhichScreen))
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
    
    fun navigateToAudioSelection(ringtoneType: String) {
        navController.navigate(Routes.AudioSelectionRoute(ringtoneType))
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