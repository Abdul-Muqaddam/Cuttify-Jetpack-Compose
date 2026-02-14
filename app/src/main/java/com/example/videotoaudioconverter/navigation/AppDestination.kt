package com.example.videotoaudioconverter.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

class AppDestination(private val navController: NavController) {

    fun navigateToMainScreen() {
        navController.navigate(Routes.MainScreenRoute) {
            popUpTo(Routes.SplashScreenRoute) {
                inclusive = true
            }
        }
    }

    fun navigateToSettingsScreen() {
        navController.navigate(Routes.SettingScreenRoute)
    }
    fun navigateToPremiumScreen() {
        navController.navigate(Routes.PremiumScreenRoute)
    }
    fun navigateToAudioMergeScreen() {
        navController.navigate(Routes.AudioMergeScreenRoute)
    }
    fun navigateToPrivacyPolicyScreen() {
        navController.navigate(Routes.PrivacyPolicyScreenRoute)
    }

    fun navigateToLanguageScreen() {
        navController.navigate(Routes.LanguageScreenRoute)
    }

    fun navigateToFeedbackScreen() {
        navController.navigate(Routes.FeedbackScreenRoute)
    }

    fun navigateToRateUsScreen() {
        navController.navigate(Routes.RateUsScreenRoute)
    }

    fun navigateToSetRingtoneScreen() {
        navController.navigate(Routes.SetRingtoneScreenRoute)
    }

    fun navigateToAudioPlayerScreen() {
        navController.navigate(Routes.AudioPlayerRoute)
    }

    fun navigateToVideoToAudioConverterScreen(fromWhichScreen: String) {
        navController.navigate(Routes.VideoToAudioConverterRoute(fromWhichScreen))
    }

    fun navigateToVideoPlayerScreen(videoUri: String) {
        navController.navigate(Routes.VideoPlayerRoute(videoUri))
    }

    fun navigateToAllVideosOfFolderScreen(folderPath: String, fromWhichScreen: String) {
        navController.navigate(Routes.VideosInsideTheFolderRoutes(folderPath, fromWhichScreen))
    }

    fun navigateToEachVideoPreviewAndPlayerScreen(videoUri: String, videoTitle: String) {
        navController.navigate(Routes.EachVideoPreviewAndPlayerRoute(videoUri, videoTitle))
    }

    fun navigateToSuccessScreen(fileName: String, filePath: String) {
        navController.navigate(Routes.SuccessScreenRoute(fileName, filePath))
    }

    fun navigateToAudioSelection(ringtoneType: String) {
        navController.navigate(Routes.AudioSelectionRoute(ringtoneType))
    }

    fun navigateToAudioCutterSelection() {
        navController.navigate(Routes.AudioCutterSelectionRoute("audio_cutter"))
    }

    fun navigateToAudioCutterScreen(audioPath: String, audioTitle: String) {
        navController.navigate(Routes.AudioCutterScreenRoute(audioPath, audioTitle))
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateBackToMain() {
        navController.navigate(Routes.MainScreenRoute) {
            popUpTo(Routes.MainScreenRoute) {
                inclusive = true
            }
        }
    }
}
