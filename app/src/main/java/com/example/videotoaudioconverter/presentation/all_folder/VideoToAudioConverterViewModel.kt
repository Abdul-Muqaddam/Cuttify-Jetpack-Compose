package com.example.videotoaudioconverter.presentation.all_folder

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
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
    val allVideos: List<VideoFiles> = emptyList(),
    val filteredVideos: List<VideoFiles> = emptyList(),
    val fileTitle: String = ""
)

data class VideoFiles(
    val uri: Uri,
    val fileName: String
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

    fun onSearchChange(value: String) {
        viewModelScope.launch(Dispatchers.IO) {

            _state.update {

                val filtered = if (value.isBlank()) {
                    it.allVideos
                } else {
                    it.allVideos.filter { uri ->
                        uri.fileName.contains(value, ignoreCase = true)
                        //                        getFileName(context, uri).contains(value, ignoreCase = true)
                    }
                }

                it.copy(
                    searchText = value,
                    filteredVideos = filtered
                )
            }
        }
    }

    fun saveAllVideos(context: Context, videos: List<Uri>) {

        viewModelScope.launch(Dispatchers.IO) {
            val videosFile = videos.map {
                VideoFiles(
                    uri = it,
                    fileName = getFileName(context = context, uri = it)
                )
            }
            _state.update {
                it.copy(
                    allVideos = videosFile,
                    filteredVideos = videosFile
                )
            }
        }
    }
}