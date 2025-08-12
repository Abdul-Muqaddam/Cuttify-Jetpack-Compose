package com.example.videotoaudioconverter.presentation.success_screen

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


data class SuccessScreenViewModelState(
    val isPlaying: Boolean = false,
    val lastPosition : Int =0,
    var formatedDuration: String ="00:00",
    var formatedCurrentPosition:String="00:00"


    )

class SuccessScreenViewModel : ViewModel(){
    private val _state = MutableStateFlow(SuccessScreenViewModelState())
    val state: StateFlow<SuccessScreenViewModelState> get() =_state

    fun isPlayingUpdate(state: Boolean){
        _state.update {
            it.copy(
                isPlaying = state
            )
        }
    }
    fun lastPositionUpdate(lastPosition:Int){
        _state.update {
            it.copy(
                lastPosition = lastPosition
            )
        }
    }
    fun formatedDurationUpdate(formatedDuration:String){
        _state.update {
            it.copy(
                formatedDuration=formatedDuration
            )
        }
    }

    fun formatedCurrentPositionUpdate(formatedCurrentPosition: String){
        _state.update {
            it.copy(
                formatedCurrentPosition=formatedCurrentPosition
            )
        }
    }
}