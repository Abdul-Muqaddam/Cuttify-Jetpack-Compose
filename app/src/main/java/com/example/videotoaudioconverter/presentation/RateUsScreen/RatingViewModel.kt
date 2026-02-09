package com.example.videotoaudioconverter.presentation.RateUsScreen

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


data class RatingUiState(
    val selectedstars : Int = 3
)

class RatingViewModel : ViewModel() {
    private val _state = MutableStateFlow(RatingUiState())
    val state: StateFlow<RatingUiState> = _state

    fun onStarSelected(stars: Int) {
        _state.update {
            it.copy(selectedstars = stars)
        }
    }
}