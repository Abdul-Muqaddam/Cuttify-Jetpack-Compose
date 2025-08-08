package com.example.videotoaudioconverter.navigation

import kotlinx.serialization.Serializable

sealed class Routes() {
    @Serializable
    object SplashScreenRoute

    @Serializable
    object HomeScreenRoute

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
