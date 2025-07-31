package com.example.videotoaudioconverter.presentation.all_folder

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videotoaudioconverter.presentation.all_video_files.components.getFileName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class VideoToAudioConverterViewModelState(
    val IdealTopBar: Boolean = true,
    val SearchTopBar: Boolean = false,
    val searchText: String = "",
    val allVideos: List<Uri> = emptyList(),
    val filteredVideos: List<Uri> = emptyList(),
    val fileTitle: String = ""
)

class VideoToAudioConverterViewModel : ViewModel() {
    private val _state = MutableStateFlow(VideoToAudioConverterViewModelState())
    val state: StateFlow<VideoToAudioConverterViewModelState> get() = _state

    fun SearchIconClicked() {
        _state.update {
            it.copy(
                IdealTopBar = false,
                SearchTopBar = true
            )
        }
    }

    fun CrossIconClicked() {
        _state.update {
            it.copy(
                IdealTopBar = true,
                SearchTopBar = false
            )
        }
    }

    fun onSearchChange(context: Context, value: String) {
        viewModelScope.launch(Dispatchers.IO) {

            _state.update {

                val filtered = if (value.isBlank()) {
                    it.allVideos
                } else {
                    it.allVideos.filter { uri ->
                        getFileName(context, uri).contains(value, ignoreCase = true)
                    }
                }

                it.copy(
                    searchText = value,
                    filteredVideos = filtered
                )
            }
        }
    }

    fun saveAllVideos(videos: List<Uri>) {
        _state.update {
            it.copy(
                allVideos = videos,
                filteredVideos = videos
            )
        }
    }


}