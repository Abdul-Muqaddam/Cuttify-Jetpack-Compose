package com.example.videotoaudioconverter.navigation

import kotlinx.serialization.Serializable

sealed class Routes() {
    @Serializable
    data class EachVideoPreviewAndPlayerRoute(
        val videoUriString: String,
        val videoTitle: String
    )

    @Serializable
    data class SuccessScreenRoute(val fileName: String,val filePath: String)
    @Serializable
    object SplashScreenRoute

    @Serializable
    object SettingScreenRoute

    @Serializable
    object MainScreenRoute

    @Serializable
    object VideoToAudioConverterRoute

    @Serializable
    object LanguageScreenRoute

    @Serializable
    object FeedbackScreenRoute

    @Serializable
    object SetRingtoneScreenRoute
    @Serializable
    object RateUsScreenRoute
}
