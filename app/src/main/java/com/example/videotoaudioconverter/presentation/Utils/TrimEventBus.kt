package com.example.videotoaudioconverter.presentation.Utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object TrimEventBus {

    sealed class TrimEvent {
        data class Progress(val value: Int) : TrimEvent()
        data class Success(val filePath: String) : TrimEvent()
        data class Error(val message: String) : TrimEvent()
    }

    private val _events = MutableSharedFlow<TrimEvent>(extraBufferCapacity = 10)
    val events = _events.asSharedFlow()

    fun sendProgress(progress: Int) {
        _events.tryEmit(TrimEvent.Progress(progress))
    }

    fun sendSuccess(filePath: String) {
        _events.tryEmit(TrimEvent.Success(filePath))
    }

    fun sendError(message: String) {
        _events.tryEmit(TrimEvent.Error(message))
    }
}