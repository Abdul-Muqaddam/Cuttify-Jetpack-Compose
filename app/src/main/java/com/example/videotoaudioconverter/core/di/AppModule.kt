package com.example.videotoaudioconverter.core.di

import com.example.videotoaudioconverter.presentation.all_folder.VideoToAudioConverterViewModel
import com.example.videotoaudioconverter.presentation.audio_player_screen.AudioPlayerViewModel
import com.example.videotoaudioconverter.presentation.comman_components.BottomSheetViewModel
import com.example.videotoaudioconverter.presentation.each_video_preview_and_player_screen.EachVideoPreviewAndPlayerScreenViewModel
import com.example.videotoaudioconverter.presentation.success_screen.SuccessScreenViewModel
import com.example.videotoaudioconverter.presentation.videos_inside_the_folder_screen.VideoInsideTheFolderViewModel
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
}