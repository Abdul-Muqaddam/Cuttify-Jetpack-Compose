package com.example.videotoaudioconverter.presentation.audio_player_screen

import android.app.Application
import android.content.ComponentName
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.result.launch
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.example.videotoaudioconverter.presentation.audio_player_screen.component.AudioFile
import com.example.lifelinepro.presentation.comman.formatDurationAudio
import com.example.videotoaudioconverter.player.AudioController
import com.example.videotoaudioconverter.presentation.success_screen.formatDurationAudio
import com.example.videotoaudioconverter.service.AudioPlayerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.ContentValues
import android.os.Environment
import android.media.MediaScannerConnection
import androidx.core.content.FileProvider
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.IOException
import java.io.File

data class AudioPlayerViewModelState(
    val allAudioFiles: List<AudioFile> = emptyList(),
    val filteredAudioFiles: List<AudioFile> = emptyList(),
    val currentlyPlayingAudio: AudioFile? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPositionFormatted: String = "0:00",
    val durationFormatted: String = "0:00",
    val searchText: String = "",
    val IdealTopBar: Boolean = true,
    val isSearching: Boolean = false,
    val showOpenSettingsOption: Boolean = false,
    val showPermissionRequest: Boolean = false,
    val permissionType: String = ""
)
//
//class AudioPlayerViewModel(application: Application) : AndroidViewModel(application) {
//    private val _state = MutableStateFlow(AudioPlayerViewModelState())
//    val state: StateFlow<AudioPlayerViewModelState> get() = _state
//
//    private var controllerFuture: ListenableFuture<MediaController>? = null
//    private val controller: MediaController?
//        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null
//
//    private var progressJob: Job? = null
//
//    init {
//        initializeController()
//    }
//
//    @OptIn(UnstableApi::class)
//    private fun initializeController() {
//        val context = getApplication<Application>()
//        val sessionToken = SessionToken(
//            context,
//            ComponentName(context, AudioPlayerService::class.java)
//        )
//
//        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
//        controllerFuture?.addListener({
//            // Set up listener for player state changes (syncs Service state to UI)
//            controller?.addListener(object : Player.Listener {
//                override fun onIsPlayingChanged(isPlaying: Boolean) {
//                    _state.update { it.copy(isPlaying = isPlaying) }
//                    if (isPlaying){ startProgressTracking() }
//                    else progressJob?.cancel()
//                }
//                override fun onPlaybackStateChanged(playbackState: Int) {
//                    when (playbackState) {
//                        Player.STATE_READY -> {
//                            val dur = controller?.duration ?: 0L
//                            if (dur > 0) {
//                                _state.update {
//                                    it.copy(
//                                        duration = dur.toInt(),
//                                        durationFormatted = formatDurationAudio(dur)
//                                    )
//                                }
//                            }
//                        }
//                        Player.STATE_BUFFERING -> {
//                            _state.update { it.copy(isLoading = true) }
//                        }
//                        Player.STATE_ENDED -> {
//                            _state.update { it.copy(isPlaying = false, currentPosition = 0) }
//                        }
//                    }
//                }
//
////                override fun onPlaybackStateChanged(playbackState: Int) {
////                    if (playbackState == Player.STATE_READY) {
////                        _state.update {
////                            it.copy(
////                                duration = controller?.duration?.toInt() ?: 0,
////                                durationFormatted = formatDurationAudio(controller?.duration ?: 0)
////                            )
////                        }
////                    } else if (playbackState == Player.STATE_ENDED) {
////                        _state.update { it.copy(isPlaying = false, currentPosition = 0) }
////                    }
////                }
//            })
//        }, MoreExecutors.directExecutor())
//    }
//
//    @OptIn(UnstableApi::class)
//    fun playAudio(audioFile: AudioFile) {
//        val context = getApplication<Application>()
//
//        viewModelScope.launch(Dispatchers.IO) {
//        val uriString = audioFile.uri?.toString() ?: ""
//        if (uriString.isBlank()) {
//            _state.update { it.copy(errorMessage = "Audio file has no valid URI") }
//            return@launch
//        }
//
//        Log.d("AudioPlayerViewModel", "Playing: ${audioFile.title}, URI: $uriString")
//
//            val serviceIntent = Intent(context, AudioPlayerService::class.java).apply {
//                putExtra("AUDIO_URI", uriString)
//                putExtra("TITLE", audioFile.title)
//                putExtra("ARTIST", audioFile.artist)
//            }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent)
//            } else {
//                context.startService(serviceIntent)
//            }
//            withContext(Dispatchers.Main) {
//                _state.update {
//                    it.copy(
//                        currentlyPlayingAudio = audioFile,
//                        isPlaying = true,
//                        errorMessage = null
//                    )
//                }
//            }
//        }
//    }
//
//
////        // Use AudioController to play with metadata
////        AudioController.play(
////            context = context,
////            uri = uriString,
////            title = audioFile.title,
////            artist = audioFile.artist
////        )
////
////        // Update UI state
////        _state.update {
////            it.copy(
////                currentlyPlayingAudio = audioFile,
////                isPlaying = true,
////                errorMessage = null
////            )
////        }
////    }
//
//
////    @OptIn(UnstableApi::class)
////    fun playAudio(audioFile: AudioFile) {
////        val context = getApplication<Application>()
////
////        // FIXED: Check if URI is valid (works for any type)
////        val uriString = audioFile.uri?.toString() ?: ""
////
////        if (uriString.isBlank()) {
////            _state.update { it.copy(errorMessage = "Audio file has no valid URI") }
////            return
////        }
////
////        // Log for debugging
////        Log.d("AudioPlayerViewModel", "Playing: ${audioFile.title}, URI: $uriString")
////
////        // Start service
////        val serviceIntent = Intent(context, AudioPlayerService::class.java).apply {
////            putExtra("AUDIO_URI", uriString)
////        }
////
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            context.startForegroundService(serviceIntent)
////        } else {
////            context.startService(serviceIntent)
////        }
////
////        // Update UI
////        _state.update {
////            it.copy(
////                currentlyPlayingAudio = audioFile,
////                isPlaying = true,
////                errorMessage = null
////            )
////        }
////    }
//
////    fun playAudio(audioFile: AudioFile) {
////        val controller = controller ?: return
////
////        // CRITICAL FIX: Check if file actually exists before telling Media3 to play it
//////        val file = File(audioFile.path)
//////        if (!file.exists()) {
//////            _state.update { it.copy(errorMessage = "File not found: ${audioFile.title}") }
//////            return
//////        }
////
////        try {
////            _state.update { it.copy(isLoading = true, errorMessage = null) }
////
////            // Use Uri.fromFile to ensure the path is correctly formatted for local storage
////            val mediaItem = MediaItem.Builder()
//////                .setMediaId(audioFile.path)
//////                .setUri(Uri.fromFile(file))
////                .setUri(audioFile.uri)
////                .setMediaMetadata(
////                    MediaMetadata.Builder()
////                        .setTitle(audioFile.title)
////                        .setArtist(audioFile.artist)
////                        .build()
////                )
////                .build()
////
////            controller.setMediaItem(mediaItem)
////            controller.prepare()
////            controller.play()
////
////            _state.update {
////                it.copy(
////                    currentlyPlayingAudio = audioFile,
////                    isPlaying = true,
////                    isLoading = false
////                )
////            }
////            startProgressTracking()
////
////        } catch (e: Exception) {
////            _state.update {
////                it.copy(errorMessage = "Playback Error: ${e.localizedMessage}", isLoading = false)
////            }
////        }
////    }
//
////    fun playAudio(audioFile: AudioFile) {
////        val controller = controller ?: return
////
////        try {
////            _state.update { it.copy(isLoading = true, errorMessage = null) }
////
////            // Create MediaItem with Metadata (This info shows in the Notification)
////            val mediaItem = MediaItem.Builder()
////                .setMediaId(audioFile.path)
////                .setUri(Uri.fromFile(File(audioFile.path)))
////                .setMediaMetadata(
////                    MediaMetadata.Builder()
////                        .setTitle(audioFile.title)
////                        .setArtist(audioFile.artist)
////                        .build()
////                )
////                .build()
////
////            controller.setMediaItem(mediaItem)
////            controller.prepare()
////            controller.play()
////
////            _state.update {
////                it.copy(
////                    currentlyPlayingAudio = audioFile,
////                    isPlaying = true,
////                    isLoading = false
////                )
////            }
////            startProgressTracking()
////
////        } catch (e: Exception) {
////            _state.update {
////                it.copy(errorMessage = "Failed to play: ${e.message}", isLoading = false)
////            }
////        }
////    }
//
//    private fun startProgressTracking() {
//        progressJob?.cancel()
//        progressJob = viewModelScope.launch {
//            while (true) {
//                controller?.let { player ->
//                    if (player.isPlaying) {
//                        _state.update {
//                            it.copy(
//                                currentPosition = player.currentPosition.toInt(),
//                                currentPositionFormatted = formatDurationAudio(player.currentPosition)
//                            )
//                        }
//                    }
//                }
//                delay(1000)
//            }
//        }
//    }
//
//    fun pauseAudio() {
//        controller?.pause()
//    }
//
//    fun resumeAudio() {
//        controller?.play()
//    }
//
//    fun togglePlayPause(audioFile: AudioFile) {
//        val currentState = _state.value
//        val currentlyPlayingUri = currentState.currentlyPlayingAudio?.uri
//        val newAudioUri = audioFile.uri
//
//        if (currentlyPlayingUri == newAudioUri) {
//            if (currentState.isPlaying) pauseAudio() else resumeAudio()
//        } else {
//            playAudio(audioFile)
//        }
//
////        if (currentState.currentlyPlayingAudio?.path == audioFile.path) {
////            if (currentState.isPlaying) pauseAudio() else resumeAudio()
////        } else {
////            playAudio(audioFile)
////        }
//    }
//
//    fun seekTo(position: Int) {
//        controller?.seekTo(position.toLong())
//        _state.update { it.copy(currentPosition = position) }
//    }
//
//    fun seekToPercentage(percentage: Float) {
//        val duration = _state.value.duration
//        if (duration > 0) {
//            seekTo((percentage * duration).toInt())
//        }
//    }
//
//    fun updateAllAudioFilter(allAudio: List<AudioFile>) {
//        _state.update { it.copy(allAudioFiles = allAudio, filteredAudioFiles = allAudio) }
//    }
//
//    fun onSearchChange(value: String) {
//        _state.update { it.copy(searchText = value, isSearching = value.isNotEmpty()) }
//        filterAudioFiles(value)
//    }
//
//    private fun filterAudioFiles(query: String) {
//        val filtered = if (query.isEmpty()) {
//            _state.value.allAudioFiles
//        } else {
//            _state.value.allAudioFiles.filter { audioFile ->
//                audioFile.title.contains(query, ignoreCase = true) ||
//                        audioFile.artist.contains(query, ignoreCase = true)
//            }
//        }
//        _state.update { it.copy(filteredAudioFiles = filtered) }
//    }
//
//    // Standard UI Logic
//    fun searchIconClicked() { _state.update { it.copy(IdealTopBar = false) } }
//    fun crossIconClicked() {
//        _state.update { it.copy(IdealTopBar = true, searchText = "", isSearching = false, filteredAudioFiles = it.allAudioFiles) }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        progressJob?.cancel()
//        controllerFuture?.let {
//            MediaController.releaseFuture(it)
//        }
//    }
//
//    // --- Ringtone / Permission logic remains the same ---
//    fun checkRingtonePermissions(context: Context): Boolean =
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.System.canWrite(context) else true
//
//    fun requestRingtonePermissions(context: Context, ringtoneType: String) {
//        if (!checkRingtonePermissions(context)) {
//            _state.update { it.copy(showPermissionRequest = true, permissionType = ringtoneType) }
//        }
//    }
//
//    fun clearPermissionRequest() { _state.update { it.copy(showPermissionRequest = false) } }
//    fun clearError() {
//        _state.update { it.copy(errorMessage = null) }
//    }
//
//}

class AudioPlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(AudioPlayerViewModelState())
    val state: StateFlow<AudioPlayerViewModelState> = _state

    // MediaController for connecting to the service
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    init {
        initializeMediaController()
    }

    @OptIn(UnstableApi::class)
    private fun initializeMediaController() {
        val context = getApplication<Application>()
        val sessionToken = SessionToken(
            context,
            ComponentName(context, AudioPlayerService::class.java)
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()

                // Listen to player state changes from service
                mediaController?.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_READY -> {
                                // Update duration if needed
                                val duration = mediaController?.duration ?: 0L
                                _state.update {
                                    it.copy(
                                        duration = duration.toInt(),
                                        durationFormatted = formatDurationAudio(duration),
                                        isLoading = false
                                    )
                                }
                            }
                            Player.STATE_BUFFERING -> {
                                _state.update { it.copy(isLoading = true) }
                            }
                            Player.STATE_ENDED -> {
                                _state.update {
                                    it.copy(
                                        isPlaying = false,
                                        currentPosition = 0,
                                        currentlyPlayingAudio = null
                                    )
                                }
                            }
                            Player.STATE_IDLE -> {
                                _state.update {
                                    it.copy(
                                        isPlaying = false,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        // Update UI when service changes play state
                        _state.update { it.copy(isPlaying = isPlaying) }
                    }
                })

                Log.d("AudioPlayerViewModel", "MediaController initialized successfully")
            } catch (e: Exception) {
                Log.e("AudioPlayerViewModel", "Failed to initialize MediaController", e)
            }
        }, MoreExecutors.directExecutor())
    }

    // Audio playback functions using AudioPlayerService
    fun togglePlayPause(audioFile: AudioFile) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("AudioDebug", "Toggle play/pause for: ${audioFile.title}")
                Log.d("AudioDebug", "Audio URI: ${audioFile.uri}")

                val context = getApplication<Application>()
                val currentState = _state.value

                // Check if this is the same audio file
                val isSameAudio = currentState.currentlyPlayingAudio?.uri == audioFile.uri

                if (isSameAudio) {
                    // Same audio - toggle play/pause
                    if (currentState.isPlaying) {
                        pauseAudio()
                    } else {
                        resumeAudio()
                    }
                } else {
                    // Different audio - play new one
                    playAudio(audioFile)
                }

            } catch (e: Exception) {
                Log.e("AudioPlayerViewModel", "Error in togglePlayPause", e)
                _state.update {
                    it.copy(
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun playAudio(audioFile: AudioFile) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update {
                    it.copy(
                        isLoading = true,
                        errorMessage = null,
                        currentlyPlayingAudio = audioFile
                    )
                }

                val context = getApplication<Application>()
                val uriString = audioFile.uri.toString()

                Log.d("AudioDebug", "Starting service with URI: $uriString")

                // Start the AudioPlayerService with the audio file
                val serviceIntent = Intent(context, AudioPlayerService::class.java).apply {
                    putExtra("AUDIO_URI", uriString)
                    putExtra("TITLE", audioFile.title)
                    putExtra("ARTIST", audioFile.artist)
                    action = "PLAY_AUDIO"
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

                // Update state to show playing
                delay(300) // Small delay for service to start
                _state.update {
                    it.copy(
                        isPlaying = true,
                        isLoading = false
                    )
                }

                Log.d("AudioDebug", "Service started successfully")

            } catch (e: Exception) {
                Log.e("AudioPlayerViewModel", "Error starting service", e)
                _state.update {
                    it.copy(
                        errorMessage = "Could not play audio. Please try again.",
                        isLoading = false,
                        isPlaying = false
                    )
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun pauseAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()

                // Send pause command to service
                val pauseIntent = Intent(context, AudioPlayerService::class.java).apply {
                    action = "PAUSE_AUDIO"
                }
                context.startService(pauseIntent)

                // Also try via controller
                mediaController?.pause()

                Log.d("AudioDebug", "Pause command sent to service")

            } catch (e: Exception) {
                Log.e("AudioPlayerViewModel", "Error pausing audio", e)
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun resumeAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()

                // Send resume command to service
                val resumeIntent = Intent(context, AudioPlayerService::class.java).apply {
                    action = "RESUME_AUDIO"
                }
                context.startService(resumeIntent)

                // Also try via controller
                mediaController?.play()

                Log.d("AudioDebug", "Resume command sent to service")

            } catch (e: Exception) {
                Log.e("AudioPlayerViewModel", "Error resuming audio", e)
            }
        }
    }

    // Stop audio completely
    @OptIn(UnstableApi::class)
    fun stopAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()

                // Send stop command to service
                val stopIntent = Intent(context, AudioPlayerService::class.java).apply {
                    action = AudioPlayerService.ACTION_STOP
                }
                context.startService(stopIntent)

                _state.update {
                    it.copy(
                        isPlaying = false,
                        currentlyPlayingAudio = null
                    )
                }

            } catch (e: Exception) {
                Log.e("AudioPlayerViewModel", "Error stopping audio", e)
            }
        }
    }

    // Duration formatting helper
    private fun formatDurationAudio(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60)) % 24

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }

    // Search and filter functions
    fun updateAllAudioFilter(allAudio: List<AudioFile>) {
        _state.update { it.copy(allAudioFiles = allAudio, filteredAudioFiles = allAudio) }
    }

    fun onSearchChange(value: String) {
        _state.update { it.copy(searchText = value, isSearching = value.isNotEmpty()) }
        filterAudioFiles(value)
    }

    private fun filterAudioFiles(query: String) {
        val filtered = if (query.isEmpty()) {
            _state.value.allAudioFiles
        } else {
            _state.value.allAudioFiles.filter { audioFile ->
                audioFile.title.contains(query, ignoreCase = true) ||
                        audioFile.artist.contains(query, ignoreCase = true)
            }
        }
        _state.update { it.copy(filteredAudioFiles = filtered) }
    }

    fun searchIconClicked() {
        _state.update { it.copy(IdealTopBar = false) }
    }

    fun crossIconClicked() {
        _state.update {
            it.copy(
                IdealTopBar = true,
                searchText = "",
                isSearching = false,
                filteredAudioFiles = it.allAudioFiles
            )
        }
    }

    // Error and permission functions
    fun clearError() {
        _state.update { it.copy(errorMessage = null, showOpenSettingsOption = false) }
    }

    fun clearPermissionRequest() {
        _state.update { it.copy(showPermissionRequest = false, permissionType = "") }
    }

    // Ringtone, Notification, Alarm functions
    fun checkRingtonePermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            true // For older versions, assume we have permission
        }
    }

    fun requestRingtonePermissions(context: Context, ringtoneType: String) {
        if (!checkRingtonePermissions(context)) {
            _state.update {
                it.copy(
                    showPermissionRequest = true,
                    permissionType = ringtoneType
                )
            }
        } else {
            // We already have permissions, so we can proceed
            _state.update {
                it.copy(
                    showPermissionRequest = false,
                    permissionType = ""
                )
            }
        }
    }

    fun openPermissionSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            _state.update { it.copy(showPermissionRequest = false) }
        } catch (e: Exception) {
            // Fallback to general settings
            try {
                val generalIntent = Intent(Settings.ACTION_SETTINGS)
                generalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(generalIntent)
                _state.update { it.copy(showPermissionRequest = false) }
            } catch (e2: Exception) {
                _state.update { it.copy(showPermissionRequest = false) }
            }
        }
    }

    fun refreshPermissions(context: Context) {
        // This method can be called when the user returns from settings
        // to check if permissions were granted
        if (checkRingtonePermissions(context)) {
            // Permissions granted, clear any permission request dialogs
            _state.update { it.copy(showPermissionRequest = false) }
        }
    }

    fun openSystemSettings() {
        // This will be called from the UI to open system settings
        _state.update { it.copy(showOpenSettingsOption = false) }
    }

    // Helper to get real file path from content URI
    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10+, use MediaStore
                val projection = arrayOf(MediaStore.Audio.Media.DATA)
                val cursor = context.contentResolver.query(uri, projection, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                        it.getString(columnIndex)
                    } else {
                        null
                    }
                }
            } else {
                // For older versions
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                        it.getString(columnIndex)
                    } else {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("RingtoneHelper", "Error getting real path from URI", e)
            null
        }
    }

    private fun trySetRingtoneDirectly(context: Context, uri: Uri, type: Int): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(context)) {
                    return false
                }
            }

            when (type) {
                RingtoneManager.TYPE_RINGTONE -> {
                    Settings.System.putString(
                        context.contentResolver,
                        Settings.System.RINGTONE,
                        uri.toString()
                    )
                }
                RingtoneManager.TYPE_NOTIFICATION -> {
                    Settings.System.putString(
                        context.contentResolver,
                        Settings.System.NOTIFICATION_SOUND,
                        uri.toString()
                    )
                }
                RingtoneManager.TYPE_ALARM -> {
                    Settings.System.putString(
                        context.contentResolver,
                        Settings.System.ALARM_ALERT,
                        uri.toString()
                    )
                }
            }

            // Verify it was set
            val ringtone = RingtoneManager.getRingtone(context, uri)
            ringtone != null
        } catch (e: Exception) {
            Log.e("RingtoneHelper", "Direct setting failed", e)
            false
        }
    }

    // REAL HELPER 2: Copy to appropriate directory and set
    private fun copyAndSetAsRingtone(context: Context, sourceFile: File, type: Int): Boolean {
        return try {
            // Determine target directory based on type
            val targetDir = when (type) {
                RingtoneManager.TYPE_RINGTONE -> Environment.DIRECTORY_RINGTONES
                RingtoneManager.TYPE_NOTIFICATION -> Environment.DIRECTORY_NOTIFICATIONS
                RingtoneManager.TYPE_ALARM -> Environment.DIRECTORY_ALARMS
                else -> Environment.DIRECTORY_RINGTONES
            }

            // Create target directory
            val publicDir = Environment.getExternalStoragePublicDirectory(targetDir)
            if (!publicDir.exists()) {
                publicDir.mkdirs()
            }

            // Copy file
            val destinationFile = File(publicDir, "Cuttify_${System.currentTimeMillis()}_${sourceFile.name}")
            sourceFile.copyTo(destinationFile, overwrite = true)

            // Scan file so system recognizes it
            MediaScannerConnection.scanFile(
                context,
                arrayOf(destinationFile.absolutePath),
                arrayOf("audio/*"),
                null
            )

            // Wait for scanning
            Thread.sleep(500)

            // Set as default using RingtoneManager
            val destinationUri = Uri.fromFile(destinationFile)
            RingtoneManager.setActualDefaultRingtoneUri(
                context,
                type,
                destinationUri
            )

            true
        } catch (e: Exception) {
            Log.e("RingtoneHelper", "Copy and set failed", e)
            false
        }
    }

    // REAL HELPER 3: Open ringtone picker as fallback
    private fun openRingtonePicker(context: Context, audioFile: AudioFile, type: Int) {
        try {
            val realPath = getRealPathFromUri(context, audioFile.uri)
            if (realPath != null) {
                val file = File(realPath)
                if (file.exists()) {
                    val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )
                    } else {
                        Uri.fromFile(file)
                    }

                    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, type)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ${audioFile.title}")
                        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, contentUri)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    context.startActivity(intent)

                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Please select this audio from the picker.",
                            showOpenSettingsOption = false
                        )
                    }
                    return
                }
            }

            // If can't open with file, open general picker
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, type)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select sound")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)

            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Sound picker opened. Please select your sound.",
                    showOpenSettingsOption = false
                )
            }

        } catch (e: Exception) {
            Log.e("AudioPlayerViewModel", "Error opening picker", e)
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Cannot open sound picker. Please set manually in Settings.",
                    showOpenSettingsOption = true
                )
            }
        }
    }

    fun setAsRingtone(audioFile: AudioFile, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                // Method 1: Try direct system setting (works on some devices with permission)
                val realPath = getRealPathFromUri(context, audioFile.uri)
                if (realPath != null) {
                    val file = File(realPath)
                    if (file.exists()) {
                        // Create content URI
                        val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                        } else {
                            Uri.fromFile(file)
                        }

                        // Method A: Try to set directly using Settings.System
                        val success = trySetRingtoneDirectly(context, contentUri, RingtoneManager.TYPE_RINGTONE)

                        if (success) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "✅ Ringtone set successfully!\nYour new ringtone is now active.",
                                    showOpenSettingsOption = false
                                )
                            }
                            return@launch
                        }

                        // Method B: Copy to ringtones directory and set
                        val ringtoneSuccess = copyAndSetAsRingtone(context, file, RingtoneManager.TYPE_RINGTONE)
                        if (ringtoneSuccess) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "✅ Ringtone set successfully!\nYour new ringtone is now active.",
                                    showOpenSettingsOption = false
                                )
                            }
                            return@launch
                        }
                    }
                }

                // Method 2: If direct methods fail, open ringtone picker
                openRingtonePicker(context, audioFile, RingtoneManager.TYPE_RINGTONE)

            } catch (e: Exception) {
                Log.e("AudioPlayerViewModel", "Error setting ringtone", e)
                // Fallback to opening picker
                openRingtonePicker(context, audioFile, RingtoneManager.TYPE_RINGTONE)
            }
        }
    }

    private fun setDefaultRingtone(context: Context, ringtoneUri: Uri): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    Settings.System.putString(
                        context.contentResolver,
                        Settings.System.RINGTONE,
                        ringtoneUri.toString()
                    )
                    return true
                }
            }

            // Fallback: Verify the ringtone is accessible
            try {
                val ringtone = android.media.RingtoneManager.getRingtone(context, ringtoneUri)
                ringtone != null
            } catch (e: Exception) {
                false
            }
        } catch (e: Exception) {
            Log.e("RingtoneHelper", "Error setting default ringtone", e)
            false
        }
    }

    // REAL METHOD 2: Set as Notification
    fun setAsNotification(audioFile: AudioFile, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                val realPath = getRealPathFromUri(context, audioFile.uri)
                if (realPath != null) {
                    val file = File(realPath)
                    if (file.exists()) {
                        val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                        } else {
                            Uri.fromFile(file)
                        }

                        // Try direct setting
                        val success = trySetRingtoneDirectly(context, contentUri, RingtoneManager.TYPE_NOTIFICATION)
                        if (success) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "✅ Notification sound set successfully!\nYour new notification sound is now active.",
                                    showOpenSettingsOption = false
                                )
                            }
                            return@launch
                        }

                        // Copy and set method
                        val notificationSuccess = copyAndSetAsRingtone(context, file, RingtoneManager.TYPE_NOTIFICATION)
                        if (notificationSuccess) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "✅ Notification sound set successfully!\nYour new notification sound is now active.",
                                    showOpenSettingsOption = false
                                )
                            }
                            return@launch
                        }
                    }
                }

                // Open notification sound picker
                openRingtonePicker(context, audioFile, RingtoneManager.TYPE_NOTIFICATION)

            } catch (e: Exception) {
                Log.e("AudioPlayerViewModel", "Error setting notification", e)
                openRingtonePicker(context, audioFile, RingtoneManager.TYPE_NOTIFICATION)
            }
        }
    }

    private fun setDefaultNotificationSound(context: Context, notificationUri: Uri): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    Settings.System.putString(
                        context.contentResolver,
                        Settings.System.NOTIFICATION_SOUND,
                        notificationUri.toString()
                    )
                    return true
                }
            }

            try {
                val ringtone = android.media.RingtoneManager.getRingtone(context, notificationUri)
                ringtone != null
            } catch (e: Exception) {
                false
            }
        } catch (e: Exception) {
            Log.e("RingtoneHelper", "Error setting notification sound", e)
            false
        }
    }

    // REAL METHOD 3: Set as Alarm
    fun setAsAlarm(audioFile: AudioFile, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                val realPath = getRealPathFromUri(context, audioFile.uri)
                if (realPath != null) {
                    val file = File(realPath)
                    if (file.exists()) {
                        val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                        } else {
                            Uri.fromFile(file)
                        }

                        // Try direct setting
                        val success = trySetRingtoneDirectly(context, contentUri, RingtoneManager.TYPE_ALARM)
                        if (success) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "✅ Alarm sound set successfully!\nYour new alarm sound is now active.",
                                    showOpenSettingsOption = false
                                )
                            }
                            return@launch
                        }

                        // Copy and set method
                        val alarmSuccess = copyAndSetAsRingtone(context, file, RingtoneManager.TYPE_ALARM)
                        if (alarmSuccess) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "✅ Alarm sound set successfully!\nYour new alarm sound is now active.",
                                    showOpenSettingsOption = false
                                )
                            }
                            return@launch
                        }
                    }
                }

                // Open alarm sound picker
                openRingtonePicker(context, audioFile, RingtoneManager.TYPE_ALARM)

            } catch (e: Exception) {
                Log.e("AudioPlayerViewModel", "Error setting alarm", e)
                openRingtonePicker(context, audioFile, RingtoneManager.TYPE_ALARM)
            }
        }
    }

    private fun setDefaultAlarmSound(context: Context, alarmUri: Uri): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    Settings.System.putString(
                        context.contentResolver,
                        Settings.System.ALARM_ALERT,
                        alarmUri.toString()
                    )
                    return true
                }
            }

            try {
                val ringtone = android.media.RingtoneManager.getRingtone(context, alarmUri)
                ringtone != null
            } catch (e: Exception) {
                false
            }
        } catch (e: Exception) {
            Log.e("RingtoneHelper", "Error setting alarm sound", e)
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up MediaController
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        // Stop audio when ViewModel is cleared
        stopAudio()
    }
}


//class AudioPlayerViewModel: ViewModel(){
//    private val _state = MutableStateFlow(AudioPlayerViewModelState())
//    val state: StateFlow<AudioPlayerViewModelState> get() = _state
//
//    private var mediaPlayer: MediaPlayer? = null
//    private var currentAudioPath: String? = null
//    private var progressJob: kotlinx.coroutines.Job? = null
//
//    fun updateAllAudioFilter(allAudio: List<AudioFile>){
//        _state.update {
//            it.copy(
//                allAudioFiles = allAudio,
//                filteredAudioFiles = allAudio
//            )
//        }
//    }
//
//    fun onSearchChange(value: String) {
//        _state.update {
//            it.copy(
//                searchText = value,
//                isSearching = value.isNotEmpty()
//            )
//        }
//        filterAudioFiles(value)
//    }
//
//    fun searchIconClicked() {
//        _state.update {
//            it.copy(IdealTopBar = false)
//        }
//    }
//
//    fun crossIconClicked() {
//        _state.update {
//            it.copy(
//                IdealTopBar = true,
//                searchText = "",
//                isSearching = false,
//                filteredAudioFiles = it.allAudioFiles
//            )
//        }
//    }
//
//    private fun filterAudioFiles(query: String) {
//        val filtered = if (query.isEmpty()) {
//            _state.value.allAudioFiles
//        } else {
//            _state.value.allAudioFiles.filter { audioFile ->
//                audioFile.title.contains(query, ignoreCase = true) ||
//                audioFile.artist.contains(query, ignoreCase = true)
//            }
//        }
//
//        _state.update {
//            it.copy(filteredAudioFiles = filtered)
//        }
//    }
//
//    fun playAudio(audioFile: AudioFile) {
//        viewModelScope.launch {
//            try {
//                _state.update { it.copy(isLoading = true, errorMessage = null) }
//
//                // Stop current playback if any
//                stopAudio()
//
//                // Create new MediaPlayer
//                mediaPlayer = MediaPlayer().apply {
//                    setDataSource(audioFile.path)
//                    prepare()
//                    start()
//                }
//
//                currentAudioPath = audioFile.path
//
//                _state.update {
//                    it.copy(
//                        currentlyPlayingAudio = audioFile,
//                        isPlaying = true,
//                        currentPosition = 0,
//                        duration = mediaPlayer?.duration ?: 0,
//                        isLoading = false,
//                        currentPositionFormatted = "0:00",
//                        durationFormatted = formatDurationAudio(mediaPlayer?.duration ?: 0)
//                    )
//                }
//
//                // Start progress tracking
//                startProgressTracking()
//
//                // Set up completion listener
//                mediaPlayer?.setOnCompletionListener {
//                    _state.update {
//                        it.copy(
//                            isPlaying = false,
//                            currentlyPlayingAudio = null,
//                            currentPosition = 0
//                        )
//                    }
//                    currentAudioPath = null
//                    progressJob?.cancel()
//                }
//
//                // Set up error listener
//                mediaPlayer?.setOnErrorListener { _, what, extra ->
//                    _state.update {
//                        it.copy(
//                            errorMessage = "Error playing audio: $what",
//                            isLoading = false,
//                            isPlaying = false
//                        )
//                    }
//                    true
//                }
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//                _state.update {
//                    it.copy(
//                        errorMessage = "Failed to load audio file: ${e.message}",
//                        isLoading = false
//                    )
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _state.update {
//                    it.copy(
//                        errorMessage = "Unexpected error: ${e.message}",
//                        isLoading = false
//                    )
//                }
//            }
//        }
//    }
//
//    private fun startProgressTracking() {
//        progressJob?.cancel()
//        progressJob = viewModelScope.launch {
//            while (true) {
//                delay(100) // Update every 100ms
//                mediaPlayer?.let { player ->
//                    if (player.isPlaying) {
//                        _state.update {
//                            it.copy(
//                                currentPosition = player.currentPosition,
//                                currentPositionFormatted = formatDurationAudio(player.currentPosition.toLong())
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    fun pauseAudio() {
//        mediaPlayer?.let { player ->
//            if (player.isPlaying) {
//                player.pause()
//                _state.update {
//                    it.copy(isPlaying = false)
//                }
//                progressJob?.cancel()
//            }
//        }
//    }
//
//    fun resumeAudio() {
//        mediaPlayer?.let { player ->
//            if (!player.isPlaying && currentAudioPath != null) {
//                player.start()
//                _state.update {
//                    it.copy(isPlaying = true)
//                }
//                startProgressTracking()
//            }
//        }
//    }
//
//    fun stopAudio() {
//        mediaPlayer?.let { player ->
//            if (player.isPlaying) {
//                player.stop()
//            }
//            player.release()
//        }
//        mediaPlayer = null
//        currentAudioPath = null
//        progressJob?.cancel()
//
//        _state.update {
//            it.copy(
//                isPlaying = false,
//                currentlyPlayingAudio = null,
//                currentPosition = 0,
//                duration = 0
//            )
//        }
//    }
//
//    fun togglePlayPause(audioFile: AudioFile) {
//        val currentState = _state.value
//
//        if (currentState.currentlyPlayingAudio?.path == audioFile.path) {
//            // Same audio file - toggle play/pause
//            if (currentState.isPlaying) {
//                pauseAudio()
//            } else {
//                resumeAudio()
//            }
//        } else {
//            // Different audio file - start playing this one
//            playAudio(audioFile)
//        }
//    }
//
//    fun clearError() {
//        _state.update { it.copy(errorMessage = null, showOpenSettingsOption = false) }
//    }
//
//    fun openSystemSettings() {
//        // This will be called from the UI to open system settings
//        // The actual implementation will be in the UI layer
//        _state.update { it.copy(showOpenSettingsOption = false) }
//    }
//
//    fun checkRingtonePermissions(context: Context): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Settings.System.canWrite(context)
//        } else {
//            true // For older versions, assume we have permission
//        }
//    }
//
//    fun requestRingtonePermissions(context: Context, ringtoneType: String) {
//        if (!checkRingtonePermissions(context)) {
//            _state.update {
//                it.copy(
//                    showPermissionRequest = true,
//                    permissionType = ringtoneType
//                )
//            }
//        } else {
//            // We already have permissions, so we can proceed
//            _state.update {
//                it.copy(
//                    showPermissionRequest = false,
//                    permissionType = ""
//                )
//            }
//        }
//    }
//
//    fun openPermissionSettings(context: Context) {
//        try {
//            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
//                data = Uri.parse("package:${context.packageName}")
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            }
//            context.startActivity(intent)
//            _state.update { it.copy(showPermissionRequest = false) }
//        } catch (e: Exception) {
//            // Fallback to general settings
//            try {
//                val generalIntent = Intent(Settings.ACTION_SETTINGS)
//                generalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(generalIntent)
//                _state.update { it.copy(showPermissionRequest = false) }
//            } catch (e2: Exception) {
//                _state.update { it.copy(showPermissionRequest = false) }
//            }
//        }
//    }
//
//    fun refreshPermissions(context: Context) {
//        // This method can be called when the user returns from settings
//        // to check if permissions were granted
//        if (checkRingtonePermissions(context)) {
//            // Permissions granted, clear any permission request dialogs
//            _state.update { it.copy(showPermissionRequest = false) }
//        }
//    }
//
//    fun clearPermissionRequest() {
//        _state.update { it.copy(showPermissionRequest = false, permissionType = "") }
//    }
//
//    fun getProgressPercentage(): Float {
//        val currentState = _state.value
//        return if (currentState.duration > 0) {
//            currentState.currentPosition.toFloat() / currentState.duration.toFloat()
//        } else {
//            0f
//        }
//    }
//
//    fun seekTo(position: Int) {
//        mediaPlayer?.let { player ->
//            if (position >= 0 && position <= player.duration) {
//                player.seekTo(position)
//                _state.update {
//                    it.copy(currentPosition = position)
//                }
//            }
//        }
//    }
//
//    fun seekToPercentage(percentage: Float) {
//        val currentState = _state.value
//        val duration = currentState.duration
//        if (duration > 0) {
//            val position = (percentage * duration).toInt()
//            seekTo(position)
//        }
//    }
//
//    fun setAsRingtone(audioFile: AudioFile, context: Context) {
//        viewModelScope.launch {
//            try {
//                // Check permissions first
//                if (!checkRingtonePermissions(context)) {
//                    requestRingtonePermissions(context, "ringtone")
//                    return@launch
//                }
//
//                _state.update { it.copy(isLoading = true, errorMessage = null) }
//
//                val file = File(audioFile.path)
//                if (!file.exists()) {
//                    _state.update {
//                        it.copy(
//                            errorMessage = "Audio file not found: ${audioFile.path}",
//                            isLoading = false
//                        )
//                    }
//                    return@launch
//                }
//
//                // Try to set as ringtone programmatically
//                try {
//                    // Use the original file directly instead of copying
//                    val ringtoneUri = Uri.fromFile(file)
//
//                    // Set as default ringtone
//                    val success = setDefaultRingtone(context, ringtoneUri)
//
//                    if (success) {
//                        _state.update {
//                            it.copy(
//                                isLoading = false,
//                                errorMessage = "Ringtone set successfully as default!\n\nYour new ringtone is now active and will play for incoming calls.",
//                                showOpenSettingsOption = false
//                            )
//                        }
//                    } else {
//                        _state.update {
//                            it.copy(
//                                isLoading = false,
//                                errorMessage = "Ringtone added to system but couldn't set as default.\n\nClick 'Open Settings' to set it manually as your default ringtone.",
//                                showOpenSettingsOption = true
//                            )
//                        }
//                    }
//
//                } catch (e: Exception) {
//                    // If programmatic setting fails, guide user to manual setting
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            errorMessage = "To set as ringtone:\n1. Go to Settings > Sound > Ringtone\n2. Select 'Add ringtone'\n3. Choose this audio file\n\nClick 'Open Settings' below to go directly to sound settings.",
//                            showOpenSettingsOption = true
//                        )
//                    }
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _state.update {
//                    it.copy(
//                        errorMessage = "Failed to set ringtone: ${e.message}",
//                        isLoading = false
//                    )
//                }
//            }
//        }
//    }
//
//    private fun setDefaultRingtone(context: Context, ringtoneUri: Uri): Boolean {
//        return try {
//            // Try to set as default ringtone using Settings.System
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (Settings.System.canWrite(context)) {
//                    // Use Settings.System to set the ringtone URI
//                    Settings.System.putString(context.contentResolver, Settings.System.RINGTONE, ringtoneUri.toString())
//                    return true
//                }
//            }
//
//            // Fallback: Try to verify the ringtone is accessible
//            try {
//                val ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
//                ringtone != null
//            } catch (e: Exception) {
//                false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }
//
//    fun setAsNotification(audioFile: AudioFile, context: Context) {
//        viewModelScope.launch {
//            try {
//                // Check permissions first
//                if (!checkRingtonePermissions(context)) {
//                    requestRingtonePermissions(context, "notification")
//                    return@launch
//                }
//
//                _state.update { it.copy(isLoading = true, errorMessage = null) }
//
//                val file = File(audioFile.path)
//                if (!file.exists()) {
//                    _state.update {
//                        it.copy(
//                            errorMessage = "Audio file not found",
//                            isLoading = false
//                        )
//                    }
//                    return@launch
//                }
//
//                // Try to set as notification sound
//                try {
//                    // Use the original file directly instead of copying
//                    val notificationUri = Uri.fromFile(file)
//
//                    // Set as default notification sound
//                    val success = setDefaultNotificationSound(context, notificationUri)
//
//                    if (success) {
//                        _state.update {
//                            it.copy(
//                                isLoading = false,
//                                errorMessage = "Notification sound set successfully as default!\n\nYour new notification sound is now active and will play for notifications.",
//                                showOpenSettingsOption = false
//                            )
//                        }
//                    } else {
//                        _state.update {
//                            it.copy(
//                                isLoading = false,
//                                errorMessage = "Notification sound added to system but couldn't set as default.\n\nClick 'Open Settings' to set it manually as your default notification sound.",
//                                showOpenSettingsOption = true
//                            )
//                        }
//                    }
//
//                } catch (e: Exception) {
//                    // If programmatic setting fails, guide user to manual setting
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            errorMessage = "To set as notification sound:\n1. Go to Settings > Sound > Default notification sound\n2. Select 'Add sound'\n3. Choose this audio file\n\nClick 'Open Settings' below to go directly to sound settings.",
//                            showOpenSettingsOption = true
//                        )
//                    }
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _state.update {
//                    it.copy(
//                        errorMessage = "Failed to set notification sound: ${e.message}",
//                        isLoading = false
//                    )
//                }
//            }
//        }
//    }
//
//    private fun setDefaultNotificationSound(context: Context, notificationUri: Uri): Boolean {
//        return try {
//            // Try to set as default notification sound using Settings.System
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (Settings.System.canWrite(context)) {
//                    // Use Settings.System to set the notification sound URI
//                    Settings.System.putString(context.contentResolver, Settings.System.NOTIFICATION_SOUND, notificationUri.toString())
//                    return true
//                }
//            }
//
//            // Fallback: Try to verify the notification sound is accessible
//            try {
//                val ringtone = RingtoneManager.getRingtone(context, notificationUri)
//                ringtone != null
//            } catch (e: Exception) {
//                false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }
//
//    fun setAsAlarm(audioFile: AudioFile, context: Context) {
//        viewModelScope.launch {
//            try {
//                // Check permissions first
//                if (!checkRingtonePermissions(context)) {
//                    requestRingtonePermissions(context, "alarm")
//                    return@launch
//                }
//
//                _state.update { it.copy(isLoading = true, errorMessage = null) }
//
//                val file = File(audioFile.path)
//                if (!file.exists()) {
//                    _state.update {
//                        it.copy(
//                            errorMessage = "Audio file not found",
//                            isLoading = false
//                        )
//                    }
//                    return@launch
//                }
//
//                // Try to set as alarm sound
//                try {
//                    // Use the original file directly instead of copying
//                    val alarmUri = Uri.fromFile(file)
//
//                    // Set as default alarm sound
//                    val success = setDefaultAlarmSound(context, alarmUri)
//
//                    if (success) {
//                        _state.update {
//                            it.copy(
//                                isLoading = false,
//                                errorMessage = "Alarm sound set successfully as default!\n\nYour new alarm sound is now active and will play for alarms.",
//                                showOpenSettingsOption = false
//                            )
//                        }
//                    } else {
//                        _state.update {
//                            it.copy(
//                                isLoading = false,
//                                errorMessage = "Alarm sound added to system but couldn't set as default.\n\nClick 'Open Settings' to set it manually as your default alarm sound.",
//                                showOpenSettingsOption = true
//                            )
//                        }
//                    }
//
//                } catch (e: Exception) {
//                    // If programmatic setting fails, guide user to manual setting
//                    _state.update {
//                        it.copy(
//                            isLoading = false,
//                            errorMessage = "To set as alarm sound:\n1. Go to Settings > Sound > Alarm sound\n2. Select 'Add sound'\n3. Choose this audio file\n\nClick 'Open Settings' below to go directly to sound settings.",
//                            showOpenSettingsOption = true
//                        )
//                    }
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _state.update {
//                    it.copy(
//                        errorMessage = "Failed to set alarm sound: ${e.message}",
//                        isLoading = false
//                    )
//                }
//            }
//        }
//    }
//
//    private fun setDefaultAlarmSound(context: Context, alarmUri: Uri): Boolean {
//        return try {
//            // Try to set as default alarm sound using Settings.System
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (Settings.System.canWrite(context)) {
//                    // Use Settings.System to set the alarm sound URI
//                    Settings.System.putString(context.contentResolver, Settings.System.ALARM_ALERT, alarmUri.toString())
//                    return true
//                }
//            }
//
//            // Fallback: Try to verify the alarm sound is accessible
//            try {
//                val ringtone = RingtoneManager.getRingtone(context, alarmUri)
//                ringtone != null
//            } catch (e: Exception) {
//                false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        stopAudio()
//    }
//}