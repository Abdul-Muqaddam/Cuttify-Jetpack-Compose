package com.example.videotoaudioconverter.core.di

import com.example.videotoaudioconverter.presentation.languageScreen.LanguageScreenViewModel
import com.example.videotoaudioconverter.presentation.allFolder.VideoToAudioConverterViewModel
import com.example.videotoaudioconverter.presentation.audioPlayerScreen.AudioPlayerViewModel
import com.example.videotoaudioconverter.presentation.audio_cutter_screen.AudioCutterViewModel
import com.example.videotoaudioconverter.presentation.commanComponents.BottomSheetViewModel
import com.example.videotoaudioconverter.presentation.eachVideoPreviewAndPlayerScreen.EachVideoPreviewAndPlayerScreenViewModel
import com.example.videotoaudioconverter.presentation.successScreen.SuccessScreenViewModel
import com.example.videotoaudioconverter.presentation.videosInsideTheFolderScreen.VideoInsideTheFolderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::VideoToAudioConverterViewModel)
    viewModelOf(::EachVideoPreviewAndPlayerScreenViewModel)
    viewModelOf(::SuccessScreenViewModel)
    viewModelOf(::VideoInsideTheFolderViewModel)
    viewModelOf(::BottomSheetViewModel)
    viewModelOf(::AudioPlayerViewModel)
    viewModelOf(::AudioCutterViewModel)
    viewModel { LanguageScreenViewModel() }
}
