package com.example.videotoaudioconverter.presentation.videoCutterScreen

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.videotoaudioconverter.presentation.Utils.TrimEventBus
import com.example.videotoaudioconverter.presentation.Utils.VideoTrimmerUtil
import com.example.videotoaudioconverter.service.VideoTrimmerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class VideoCutterState(
    val videoUri: Uri? = null,
    val durationMs: Long = 0L,
    val startMs: Long = 0L,
    val endMs: Long = 0L,
    val currentPositionMs: Long = 0L,
    val thumbnails: List<Bitmap> = emptyList(),
    val isTrimming: Boolean = false,
    val trimProgress: Int = 0,
    val trimmedFile: File? = null,
    val errorMessage: String? = null,
    val isLoadingThumbnails: Boolean = false
)

class VideoCutterViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(VideoCutterState())
    val state: StateFlow<VideoCutterState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            TrimEventBus.events.collect { event ->
                when (event) {
                    is TrimEventBus.TrimEvent.Progress -> {
                        _state.value = _state.value.copy(trimProgress = event.value)
                    }
                    is TrimEventBus.TrimEvent.Success -> {
                        _state.value = _state.value.copy(
                            isTrimming = false,
                            trimProgress = 100,
                            trimmedFile = File(event.filePath)
                        )
                    }
                    is TrimEventBus.TrimEvent.Error -> {
                        _state.value = _state.value.copy(
                            isTrimming = false,
                            errorMessage = event.message
                        )
                    }
                }
            }
        }
    }

    fun loadVideo(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                videoUri = uri,
                isLoadingThumbnails = true,
                thumbnails = emptyList()
            )

            // Get video duration
            val duration = withContext(Dispatchers.IO) {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(getApplication(), uri)
                val dur = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull() ?: 0L
                retriever.release()
                dur
            }

            _state.value = _state.value.copy(
                durationMs = duration,
                startMs = 0L,
                endMs = duration,
                currentPositionMs = 0L
            )

            // Generate thumbnails
            generateThumbnails(uri, duration)
        }
    }

    private suspend fun generateThumbnails(uri: Uri, durationMs: Long) {
        withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(getApplication(), uri)
                val count = 10
                val thumbs = mutableListOf<Bitmap>()

                for (i in 0 until count) {
                    val timeUs = (durationMs * 1000L / count) * i
                    val bmp = retriever.getFrameAtTime(
                        timeUs,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )
                    if (bmp != null) thumbs.add(bmp)
                }

                retriever.release()

                _state.value = _state.value.copy(
                    thumbnails = thumbs,
                    isLoadingThumbnails = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoadingThumbnails = false)
            }
        }
    }

    fun updateStartMs(ms: Long) {
        val clamped = ms.coerceIn(0L, (_state.value.endMs - 500L).coerceAtLeast(0L))
        _state.value = _state.value.copy(startMs = clamped)
    }

    fun updateEndMs(ms: Long) {
        val clamped = ms.coerceIn(
            (_state.value.startMs + 500L),
            _state.value.durationMs
        )
        _state.value = _state.value.copy(endMs = clamped)
    }

    fun updateCurrentPosition(ms: Long) {
        _state.value = _state.value.copy(currentPositionMs = ms)
    }

    fun trimVideo(context: Context) {
        val state = _state.value
        val uri = state.videoUri ?: return

        _state.value = _state.value.copy(
            isTrimming = true,
            trimProgress = 0
        )

        // Start foreground service
        val intent = Intent(context, VideoTrimmerService::class.java).apply {
            action = VideoTrimmerService.ACTION_START_TRIM
            putExtra(VideoTrimmerService.EXTRA_VIDEO_URI, uri.toString())
            putExtra(VideoTrimmerService.EXTRA_START_MS, state.startMs)
            putExtra(VideoTrimmerService.EXTRA_END_MS, state.endMs)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun handleProgress(progress: Int) {
        _state.value = _state.value.copy(trimProgress = progress)
    }

    fun handleSuccess(filePath: String) {
        _state.value = _state.value.copy(
            isTrimming = false,
            trimProgress = 100,
            trimmedFile = File(filePath)
        )
    }

    fun handleError(message: String) {
        _state.value = _state.value.copy(
            isTrimming = false,
            errorMessage = message
        )
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun clearTrimmedFile() {
        _state.value = _state.value.copy(trimmedFile = null, trimProgress = 0)
    }

    fun resetVideo() {
        _state.value = VideoCutterState()
    }
}