package com.example.videotoaudioconverter.navigation

import kotlinx.serialization.Serializable

sealed class Routes() {
    @Serializable
    data class EachVideoPreviewAndPlayerRoute(
        val videoUriString: String,
        val videoTitle: String
    )

    @Serializable
    data object AudioPlayerRoute

    @Serializable
    data class VideoPlayerRoute(
        val videoUri:String,
    )
    @Serializable
    data class VideosInsideTheFolderRoutes(
        val folderPath:String,
        val fromWhichScreen:String
    )

    @Serializable
    data class SuccessScreenRoute(val fileName: String,val filePath: String)
    @Serializable
    object SplashScreenRoute

    @Serializable
    data object SettingScreenRoute

    @Serializable
    object MainScreenRoute

    @Serializable
    data class VideoToAudioConverterRoute(val fromWhichScreen: String)

    @Serializable
    object LanguageScreenRoute

    @Serializable
    object FeedbackScreenRoute

    @Serializable
    data object SetRingtoneScreenRoute
    
    @Serializable
    data class AudioSelectionRoute(val ringtoneType: String)
    
    @Serializable
    object RateUsScreenRoute
}
