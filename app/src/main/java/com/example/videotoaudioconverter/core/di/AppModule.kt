package com.example.videotoaudioconverter.core.di

import com.example.videotoaudioconverter.presentation.all_folder.VideoToAudioConverterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::VideoToAudioConverterViewModel)

}