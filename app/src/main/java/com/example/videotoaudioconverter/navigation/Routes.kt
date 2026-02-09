package com.example.videotoaudioconverter.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    data object SplashScreenRoute : Routes()
    
    @Serializable
    data object MainScreenRoute : Routes()

    @Serializable
    data object SettingScreenRoute : Routes()

    @Serializable
    data object PremiumScreenRoute : Routes()

    @Serializable
    data object PrivacyPolicyScreenRoute : Routes()

    @Serializable
    data object LanguageScreenRoute : Routes()

    @Serializable
    data object FeedbackScreenRoute : Routes()
    
    @Serializable
    data object RateUsScreenRoute : Routes()
    
    @Serializable
    data object SetRingtoneScreenRoute : Routes()
    
    @Serializable
    data object AudioPlayerRoute : Routes()
    
    @Serializable
    data class VideoToAudioConverterRoute(
        val fromWhichScreen: String
    ) : Routes()
    
    @Serializable
    data class VideoPlayerRoute(
        val videoUri: String
    ) : Routes()
    
    @Serializable
    data class VideosInsideTheFolderRoutes(
        val folderPath: String,
        val fromWhichScreen: String
    ) : Routes()
    
    @Serializable
    data class EachVideoPreviewAndPlayerRoute(
        val videoUriString: String,
        val videoTitle: String
    ) : Routes()
    
    @Serializable
    data class SuccessScreenRoute(
        val fileName: String,
        val filePath: String
    ) : Routes()
    
    @Serializable
    data class AudioSelectionRoute(
        val ringtoneType: String
    ) : Routes()
    
    @Serializable
    data class AudioCutterSelectionRoute(
        val cutterType: String
    ) : Routes()
    
    @Serializable
    data class AudioCutterScreenRoute(
        val audioPath: String,
        val audioTitle: String
    ) : Routes()
}

