package com.example.videotoaudioconverter.core.di

import com.example.videotoaudioconverter.presentation.all_folder.VideoToAudioConverterViewModel
import com.example.videotoaudioconverter.presentation.each_video_preview_and_player_screen.EachVideoPreviewAndPlayerScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::VideoToAudioConverterViewModel)
    viewModelOf(::EachVideoPreviewAndPlayerScreenViewModel)

}