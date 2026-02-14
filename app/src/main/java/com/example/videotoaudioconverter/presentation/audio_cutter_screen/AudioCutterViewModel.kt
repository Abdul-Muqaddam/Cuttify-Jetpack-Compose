package com.example.videotoaudioconverter.presentation.audio_cutter_screen

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.example.videotoaudioconverter.audio.AudioExporter
import com.example.videotoaudioconverter.player.AudioPreviewPlayer
import com.example.videotoaudioconverter.service.AudioTrimForegroundService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File



data class AudioCutterUiState(
    val startFraction: Float = 0.0f,
    val endFraction: Float = 1.0f,
    val totalDuration: Float = 1f,
    val isPlaying: Boolean = false,
    val isExporting: Boolean = false
)

object AudioTrimCallback {

    var viewModel: AudioCutterViewModel? = null

    fun onTrimSuccess(outputPath: String) {
        viewModel?.onAudioTrimCompleted(outputPath)
    }

    fun onTrimError(error: String) {
        viewModel?.onAudioTrimFailed(error)
    }
}

class AudioCutterViewModel(
    private val context: Context,
    application: Application
) : AndroidViewModel(application) {

    private var mediaPlayer: MediaPlayer? = null
    private val previewPlayer = AudioPreviewPlayer(application)
    private val handler = Handler(Looper.getMainLooper())
    private var isPrepared = false
    private var stopRunnable: Runnable? = null
    private val _uiState = MutableStateFlow(AudioCutterUiState())
    val uiState: StateFlow<AudioCutterUiState> = _uiState.asStateFlow()

    private var cachedInputFile: File? = null
    private var audioPath: String = ""

    fun setAudioPath(path: String) {
        audioPath = path
    }

    sealed class AudioTrimResult {
        object Idle : AudioTrimResult()
        data class Success(val outputPath: String) : AudioTrimResult()
        data class Error(val message: String) : AudioTrimResult()
    }

    private val _trimResult =
        MutableStateFlow<AudioTrimResult>(AudioTrimResult.Idle)

    val trimResult: StateFlow<AudioTrimResult> = _trimResult

    fun load(context: Context, audioUri: android.net.Uri) {

//        val cachedFile = AudioFileUtil.copyUriToCache(context, audioUri)
        cachedInputFile = AudioFileUtils.copyUriToCache(context, audioUri)

        audioPath = cachedInputFile!!.absolutePath

        Log.d("STEP_LOAD", "Cached path = $audioPath")
        Log.d("STEP_LOAD", "Exists = ${cachedInputFile!!.exists()}")

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, audioUri)
            setOnPreparedListener {
                isPrepared = true
                _uiState.update {
                    it.copy(
                        totalDuration = duration / 1000f
                    )
                }
            }
            prepareAsync()
        }
        previewPlayer.load(audioPath)
    }

    fun getAudioPath(): String {
        return audioPath
    }

    fun onStartFractionChanged(value: Float) {
        val minGap = 1f / _uiState.value.totalDuration.coerceAtLeast(1f)
        _uiState.value = _uiState.value.copy(
            startFraction = value.coerceAtMost(_uiState.value.endFraction - minGap)
        )
    }

    fun onEndFractionChanged(value: Float) {
        val minGap = 1f / _uiState.value.totalDuration.coerceAtLeast(1f)
        _uiState.value = _uiState.value.copy(
            endFraction = value.coerceAtLeast(_uiState.value.startFraction + minGap)
        )
    }

    fun onPlayPauseClicked() {
        val state = _uiState.value
        val startMs = (state.startFraction * state.totalDuration * 1000).toLong()
        val endMs = (state.endFraction * state.totalDuration * 1000).toLong()

        if (previewPlayer.isPlaying()) {
            previewPlayer.pause()
            _uiState.value = state.copy(isPlaying = false)
        } else {
            previewPlayer.play(startMs, endMs)
            _uiState.value = state.copy(isPlaying = true)
        }
    }

    private val _exportStatus = MutableStateFlow<String>("")
    val exportStatus: StateFlow<String> get() = _exportStatus

    fun exportAudio(
        outputFileName: String,
//        navigateToSuccess: () -> Unit
    ) {
        if (cachedInputFile == null || !cachedInputFile!!.exists()) {
            _exportStatus.value = "Error: input file missing"
            return
        }

        val state = _uiState.value
        val startMs = (state.startFraction * state.totalDuration * 1000).toLong()
        val endMs = (state.endFraction * state.totalDuration * 1000).toLong()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // ✅ Use existing folder path
                val musicDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Cuttify/Trimmed Audio")
                if (!musicDir.exists()) musicDir.mkdirs()

                val outputFile = File(musicDir, "$outputFileName.mp3")

                AudioExporter.trimAudio(
                    inputPath = cachedInputFile!!.absolutePath,
                    startMs = startMs,
                    endMs = endMs,
                    outputFile = outputFile
                )

//                withContext(Dispatchers.Main) {
//                    _exportStatus.value = "Audio saved at: ${outputFile.absolutePath}"
//                    // ✅ Navigate to success screen
//                    navigateToSuccess()
//                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _exportStatus.value = "Error: ${e.message}"
                }
            }
        }
    }

    fun rewind5Sec() {
        val state = _uiState.value
        val currentMs = previewPlayer.currentPosition()
        val newMs =
            (currentMs - 5000).coerceAtLeast((state.startFraction * state.totalDuration * 1000).toLong())
        previewPlayer.seekTo(newMs)
    }

    fun forward5Sec() {
        val state = _uiState.value
        val currentMs = previewPlayer.currentPosition()
        val newMs =
            (currentMs + 5000).coerceAtMost((state.endFraction * state.totalDuration * 1000).toLong())
        previewPlayer.seekTo(newMs)
    }
    fun resetTrimResult() {
        _trimResult.value = AudioTrimResult.Idle
    }

    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun startAudioTrimService(
        context: Context,
        inputPath: String,
        outputName: String,
        startMs: Long,
        endMs: Long
    ) {
        // Check if input file exists
        val inputFile = File(inputPath)
        if (!inputFile.exists()) {
            Log.e("AudioCutter", "Input file does not exist: $inputPath")
            return
        }

        // Prepare intent for the foreground service
        val intent = Intent(context, AudioTrimForegroundService::class.java).apply {
            putExtra(AudioTrimForegroundService.EXTRA_INPUT_PATH, inputFile.absolutePath)
            putExtra(AudioTrimForegroundService.EXTRA_OUTPUT_NAME, outputName)
            putExtra(AudioTrimForegroundService.EXTRA_START_MS, startMs)
            putExtra(AudioTrimForegroundService.EXTRA_END_MS, endMs)
        }

        // Start the foreground service
        ContextCompat.startForegroundService(context, intent)

        // Optional: update UI state if you have a MutableStateFlow in ViewModel
        _uiState.update { it.copy(isExporting = true) }
    }

    fun onAudioTrimCompleted(outputPath: String) {
        _trimResult.value = AudioTrimResult.Success(outputPath)
    }

    fun onAudioTrimFailed(error: String) {
        _trimResult.value = AudioTrimResult.Error(error)
    }

    override fun onCleared() {
        super.onCleared()
        previewPlayer.release()
    }
}


//data class AudioCutterUiState(
//    val startFraction: Float = 0.1f,
//    val endFraction: Float = 0.9f,
//    val totalDuration: Float = 1f,
//    val isPlaying: Boolean = false,
//    val isExporting: Boolean = false
//)
//
//class AudioCutterViewModel(
//    private val context: Context,
//    application: Application
//) : AndroidViewModel(application) {
//
//    private var mediaPlayer: MediaPlayer? = null
//
//    private var cachedAudioFile: File? = null
//
//    private val previewPlayer = AudioPreviewPlayer(application)
//
//    private val _uiState = MutableStateFlow(AudioCutterUiState())
//    val uiState = _uiState.asStateFlow()
//
//    private var audioPath: String = ""
//    fun getAudioPath(): String = audioPath
//    fun setAudioPath(path: String) {
//        audioPath = path
//    }
//
//    fun load(uriString: String) {
//        val uri = android.net.Uri.parse(uriString)
//
//        cachedAudioFile = AudioFileUtil.copyUriToCache(context, uri)
//
//        val path = cachedAudioFile!!.absolutePath
//        audioPath = path
//
//        mediaPlayer?.release()
//        mediaPlayer = MediaPlayer().apply {
//            setDataSource(path)
//            prepare()
//        }
//
//        val durationSec = (mediaPlayer?.duration ?: 0) / 1000f
//            .coerceAtLeast(1f)
//
//        _uiState.value = _uiState.value.copy(
//            totalDuration = durationSec,
//            startFraction = 0f,
//            endFraction = 1f,
//            isPlaying = false
//        )
//
//        previewPlayer.load(path)
//    }
//    fun onStartFractionChanged(value: Float) {
//        val minGap = 1f / _uiState.value.totalDuration.coerceAtLeast(1f)
//        _uiState.value = _uiState.value.copy(
//            startFraction = value.coerceAtMost(_uiState.value.endFraction - minGap)
//        )
//    }
//
//    fun onEndFractionChanged(value: Float) {
//        val minGap = 1f / _uiState.value.totalDuration.coerceAtLeast(1f)
//        _uiState.value = _uiState.value.copy(
//            endFraction = value.coerceAtLeast(_uiState.value.startFraction + minGap)
//        )
//    }
//
//
//    fun onPlayPauseClicked() {
//        val state = _uiState.value
//
//        val startMs =
//            (state.startFraction * state.totalDuration * 1000).toLong()
//
//        val endMs =
//            (state.endFraction * state.totalDuration * 1000).toLong()
//
//        if(previewPlayer.isPlaying()){
//            previewPlayer.pause()
//            _uiState.value = state.copy(isPlaying = false)
//        }else{
//            previewPlayer.play(startMs,endMs)
//            _uiState.value = state.copy(isPlaying = true)
//        }
//    }
//
//    private val _exportStatus = MutableStateFlow<String>("")
//    val exportStatus: StateFlow<String> get() = _exportStatus
//
//    fun exportAudio(inputPath: String, startMs: Long, endMs: Long, outputFileName: String) {
//
//
//        Log.d("STEP2", "exportAudio() CALLED")
//        Log.d("STEP3", "Input path = $inputPath")
//
//        val inputFile = File(inputPath)
//        Log.d("STEP3", "Input exists = ${inputFile.exists()}")
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val outputFile = AudioExporter.trimAudio(
//                    context = getApplication(),
//                    inputPath = inputPath,
//                    startMs = startMs,
//                    endMs = endMs,
//                    outputFileName = outputFileName
//                )
//
//                withContext(Dispatchers.Main) {
//                    _exportStatus.value = "Audio saved: ${outputFile.absolutePath}"
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    _exportStatus.value = "Error: ${e.message}"
//                }
//            }
//        }
//    }
//
//
//    fun getAppCacheDir(): File {
//        return getApplication<Application>().cacheDir
//    }
//
//    fun rewind5Sec() {
//        val state = _uiState.value
//        val currentMs = previewPlayer.currentPosition()
//        val newMs =
//            (currentMs - 5000).coerceAtLeast((state.startFraction * state.totalDuration * 1000).toLong())
//        previewPlayer.seekTo(newMs)
//    }
//
//    fun forward5Sec() {
//        val state = _uiState.value
//        val currentMs = previewPlayer.currentPosition()
//        val newMs =
//            (currentMs + 5000).coerceAtMost((state.endFraction * state.totalDuration * 1000).toLong())
//        previewPlayer.seekTo(newMs)
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        previewPlayer.release()
//    }
//}