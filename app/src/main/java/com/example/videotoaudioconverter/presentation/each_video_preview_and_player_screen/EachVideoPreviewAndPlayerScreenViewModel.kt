package com.example.videotoaudioconverter.presentation.each_video_preview_and_player_screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class EachVideoPreviewAndPlayerScreenViewModelState(
    val videoFileNameWithoutExtension: String = "",
    val formatList: List<String> = listOf("MP3", "AAC", "WAV", "OGG", "FLAC", "M4A"),
    val bitrateList:List<Int> = listOf(64, 96, 128, 192, 256, 320),
    val selectedFormatItem:String=formatList[0],
    val selectedBitrateItem:Int=bitrateList[0],
    val formatDropDownState: Boolean = false,
    val bitrateDropDownState: Boolean = false,
)

class EachVideoPreviewAndPlayerScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(EachVideoPreviewAndPlayerScreenViewModelState())
    val state: StateFlow<EachVideoPreviewAndPlayerScreenViewModelState> get() = _state

    fun setVideoFileNameWithoutExtension(reNamedVideoFileName: String) {
        _state.update {
            it.copy(
                videoFileNameWithoutExtension = reNamedVideoFileName
            )
        }
    }


    fun formatDropDownStateUpdate(state: Boolean) {
        _state.update {
            it.copy(
                formatDropDownState = state
            )
        }
    }

    fun bitrateDropDownStateUpdate(state: Boolean) {
        _state.update {
            it.copy(
                bitrateDropDownState =  state
            )
        }
    }

    fun selectedFormatItemChange(item: String){
        _state.update {
            it.copy(
                selectedFormatItem = item
            )
        }
    }
    fun selectedBitrateItemChange(item: Int){
        _state.update {
            it.copy(
                selectedBitrateItem = item
            )
        }
    }
}