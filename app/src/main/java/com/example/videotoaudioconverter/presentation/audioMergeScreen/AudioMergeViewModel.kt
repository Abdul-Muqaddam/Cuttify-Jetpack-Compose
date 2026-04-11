package com.example.videotoaudioconverter.presentation.audioMergeScreen

import android.app.Application
import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import com.arthenica.ffmpegkit.Statistics
import com.example.videotoaudioconverter.presentation.audio_cutter_screen.AudioFileUtils
import com.example.videotoaudioconverter.service.AudioMergeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer

data class AudioMergerUiState(
    val selectedAudios: List<Uri> = emptyList(),
    val isMerging: Boolean = false,
    val mergeResult: MergeResult = MergeResult.Idle
)

sealed class MergeResult {
    object Idle : MergeResult()
    data class Success(val outputPath: String) : MergeResult()
    data class Error(val message: String) : MergeResult()
}

class AudioMergerViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AudioMergerUiState())
    val uiState: StateFlow<AudioMergerUiState> = _uiState.asStateFlow()

    fun addAudio(uri: Uri) {
        val current = _uiState.value.selectedAudios.toMutableList()
        current.add(uri)
        _uiState.value = _uiState.value.copy(selectedAudios = current)
    }

    fun removeAudio(uri: Uri) {
        val current = _uiState.value.selectedAudios.toMutableList()
        current.remove(uri)
        _uiState.value = _uiState.value.copy(selectedAudios = current)
    }

    fun resetResult() {
        _uiState.value = _uiState.value.copy(mergeResult = MergeResult.Idle)
    }


    fun mergeAudios(context: Context, outputFileName: String) {
        val uris = _uiState.value.selectedAudios
        if (uris.size < 2) return

        _uiState.value = _uiState.value.copy(isMerging = true, mergeResult = MergeResult.Idle)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Step 1: Copy URIs to cache
                val cachedFiles = uris.map { uri -> AudioFileUtils.copyUriToCache(context, uri) }

                var totalDurationMs = 0L

                cachedFiles.forEach { file ->
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(file.absolutePath)
                    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    totalDurationMs += durationStr?.toLong() ?: 0L
                    retriever.release()
                }

                // Step 2: Prepare output folder in MediaStore
                val resolver = context.contentResolver
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.Audio.Media.DISPLAY_NAME, "$outputFileName.mp3")
                    put(android.provider.MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
                    put(
                        android.provider.MediaStore.Audio.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_MUSIC + "/Cuttify/MergedAudio"
                    )
                    put(android.provider.MediaStore.Audio.Media.IS_PENDING, 1)
                }

                val outputUri = resolver.insert(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: throw Exception("Failed to create MediaStore record")

                val tempOutputFile = File(context.cacheDir, "$outputFileName.mp3")

                // Step 3: Prepare FFmpeg input list
                val listFile = File(context.cacheDir, "ffmpeg_list.txt")
                listFile.bufferedWriter().use { writer ->
                    cachedFiles.forEach { file ->
                        writer.write("file '${file.absolutePath}'\n")
                    }
                }

                // Step 4: FFmpeg command
                val command = "-f concat -safe 0 -i \"${listFile.absolutePath}\" -c:a libmp3lame -b:a 192k \"${tempOutputFile.absolutePath}\""


                var lastProgress = 0

                FFmpegKitConfig.enableStatisticsCallback { statistics ->

                    val currentTimeMs = statistics.time

                    if (totalDurationMs > 0) {

                        var progress = ((currentTimeMs.toFloat() / totalDurationMs.toFloat()) * 100)
                            .toInt()
                            .coerceIn (0, 99)   // 🔥 Never allow 100 here

                        // 🔥 Prevent backward progress
                        if (progress < lastProgress) {
                            progress = lastProgress
                        }

                        lastProgress = progress

                        AudioMergeService.instance?.updateProgress(progress)
                    }
                }

                // Step 5: Run FFmpeg asynchronously to get progress
                FFmpegKit.executeAsync(
                    command,
                    { session ->
                        val returnCode = session.returnCode

                        if (ReturnCode.isSuccess(returnCode)) {

                            AudioMergeService.instance?.updateProgress(100)

                            resolver.openOutputStream(outputUri)?.use { outputStream ->
                                tempOutputFile.inputStream().use { inputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }

                            contentValues.clear()
                            contentValues.put(android.provider.MediaStore.Audio.Media.IS_PENDING, 0)
                            resolver.update(outputUri, contentValues, null, null)
                            tempOutputFile.delete()

                            viewModelScope.launch(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(
                                    isMerging = false,
                                    mergeResult = MergeResult.Success("Saved in Music/Cuttify/MergedAudio")
                                )
                                AudioMergeService.instance?.showSuccess()
                            }

                        } else {

                            val failMessage = session.failStackTrace ?: "Unknown error"

                            viewModelScope.launch(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(
                                    isMerging = false,
                                    mergeResult = MergeResult.Error("Merge failed: $failMessage")
                                )
                                AudioMergeService.instance?.stopSelf()
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isMerging = false,
                        mergeResult = MergeResult.Error("Error: ${e.message}")
                    )
                    AudioMergeService.instance?.stopSelf()
                }
            }
        }
    }

//    fun mergeAudios(context: Context, outputFileName: String) {
//        val uris = _uiState.value.selectedAudios
//        if (uris.size < 2) return
//
//        _uiState.value = _uiState.value.copy(isMerging = true, mergeResult = MergeResult.Idle)
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // Step 1: Copy URIs to cache
//                val cachedFiles = uris.map { uri ->
//                    AudioFileUtils.copyUriToCache(context, uri)
//                }
//
//                // Step 2: Create output folder
//                val resolver = context.contentResolver
//
//                val contentValues = android.content.ContentValues().apply {
//                    put(android.provider.MediaStore.Audio.Media.DISPLAY_NAME, "$outputFileName.mp3")
//                    put(android.provider.MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
//                    put(
//                        android.provider.MediaStore.Audio.Media.RELATIVE_PATH,
//                        Environment.DIRECTORY_MUSIC + "/Cuttify/MergedAudio"
//                    )
//                    put(android.provider.MediaStore.Audio.Media.IS_PENDING, 1)
//                }
//
//                val uri = resolver.insert(
//                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    contentValues
//                ) ?: throw Exception("Failed to create MediaStore record")
//
//// Create temporary file in cache for FFmpeg output
//                val tempOutputFile = File(context.cacheDir, "$outputFileName.mp3")
//
//                // Step 3: Prepare FFmpeg input list
//                // Create a text file with list of input files
//                val listFile = File(context.cacheDir, "ffmpeg_list.txt")
//                listFile.bufferedWriter().use { writer ->
//                    cachedFiles.forEach { file ->
//                        writer.write("file '${file.absolutePath}'\n")
//                    }
//                }
//
//                // Step 4: Run FFmpeg command to merge all files into one AAC (.m4a)
//                // - safe 0: allows spaces in path
//                val command = "-f concat -safe 0 -i \"${listFile.absolutePath}\" -c:a libmp3lame -b:a 192k \"${tempOutputFile.absolutePath}\""
//
//                val session = FFmpegKit.execute(command)
//
//                val returnCode = session.returnCode
//                if (ReturnCode.isSuccess(returnCode)) {
//                    // Success
//                    resolver.openOutputStream(uri)?.use { outputStream ->
//                        tempOutputFile.inputStream().use { inputStream ->
//                            inputStream.copyTo(outputStream)
//                        }
//                    }
//
//                    contentValues.clear()
//                    contentValues.put(android.provider.MediaStore.Audio.Media.IS_PENDING, 0)
//                    resolver.update(uri, contentValues, null, null)
//
//                    tempOutputFile.delete()
//
//                    withContext(Dispatchers.Main) {
//                        _uiState.value = _uiState.value.copy(
//                            isMerging = false,
//                            mergeResult = MergeResult.Success("Saved in Music/Cuttify/MergedAudio")
//                        )
//                    }
//                } else {
//                    // Error
//                    val failMessage = session.failStackTrace ?: "Unknown error"
//                    withContext(Dispatchers.Main) {
//                        _uiState.value = _uiState.value.copy(
//                            isMerging = false,
//                            mergeResult = MergeResult.Error("Merge failed: $failMessage")
//                        )
//                    }
//                }
//
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    _uiState.value = _uiState.value.copy(
//                        isMerging = false,
//                        mergeResult = MergeResult.Error("Error: ${e.message}")
//                    )
//                }
//            }
//        }
//    }
}