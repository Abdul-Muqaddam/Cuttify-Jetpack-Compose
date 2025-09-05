package com.example.videotoaudioconverter.presentation.audio_player_screen

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videotoaudioconverter.presentation.audio_player_screen.component.AudioFile
import com.example.lifelinepro.presentation.comman.formatDurationAudio
import com.example.videotoaudioconverter.presentation.success_screen.formatDurationAudio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
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

class AudioPlayerViewModel: ViewModel(){
    private val _state = MutableStateFlow(AudioPlayerViewModelState())
    val state: StateFlow<AudioPlayerViewModelState> get() = _state
    
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudioPath: String? = null
    private var progressJob: kotlinx.coroutines.Job? = null

    fun updateAllAudioFilter(allAudio: List<AudioFile>){
        _state.update {
            it.copy(
                allAudioFiles = allAudio,
                filteredAudioFiles = allAudio
            )
        }
    }
    
    fun onSearchChange(value: String) {
        _state.update { 
            it.copy(
                searchText = value,
                isSearching = value.isNotEmpty()
            )
        }
        filterAudioFiles(value)
    }
    
    fun searchIconClicked() {
        _state.update { 
            it.copy(IdealTopBar = false) 
        }
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
    
    private fun filterAudioFiles(query: String) {
        val filtered = if (query.isEmpty()) {
            _state.value.allAudioFiles
        } else {
            _state.value.allAudioFiles.filter { audioFile ->
                audioFile.title.contains(query, ignoreCase = true) ||
                audioFile.artist.contains(query, ignoreCase = true)
            }
        }
        
        _state.update { 
            it.copy(filteredAudioFiles = filtered) 
        }
    }
    
    fun playAudio(audioFile: AudioFile) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                
                // Stop current playback if any
                stopAudio()
                
                // Create new MediaPlayer
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioFile.path)
                    prepare()
                    start()
                }
                
                currentAudioPath = audioFile.path
                
                _state.update {
                    it.copy(
                        currentlyPlayingAudio = audioFile,
                        isPlaying = true,
                        currentPosition = 0,
                        duration = mediaPlayer?.duration ?: 0,
                        isLoading = false,
                        currentPositionFormatted = "0:00",
                        durationFormatted = formatDurationAudio(mediaPlayer?.duration ?: 0)
                    )
                }
                
                // Start progress tracking
                startProgressTracking()
                
                // Set up completion listener
                mediaPlayer?.setOnCompletionListener {
                    _state.update {
                        it.copy(
                            isPlaying = false,
                            currentlyPlayingAudio = null,
                            currentPosition = 0
                        )
                    }
                    currentAudioPath = null
                    progressJob?.cancel()
                }
                
                // Set up error listener
                mediaPlayer?.setOnErrorListener { _, what, extra ->
                    _state.update {
                        it.copy(
                            errorMessage = "Error playing audio: $what",
                            isLoading = false,
                            isPlaying = false
                        )
                    }
                    true
                }
                
            } catch (e: IOException) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        errorMessage = "Failed to load audio file: ${e.message}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        errorMessage = "Unexpected error: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                delay(100) // Update every 100ms
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _state.update {
                            it.copy(
                                currentPosition = player.currentPosition,
                                currentPositionFormatted = formatDurationAudio(player.currentPosition.toLong())
                            )
                        }
                    }
                }
            }
        }
    }
    
    fun pauseAudio() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                _state.update {
                    it.copy(isPlaying = false)
                }
                progressJob?.cancel()
            }
        }
    }
    
    fun resumeAudio() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying && currentAudioPath != null) {
                player.start()
                _state.update {
                    it.copy(isPlaying = true)
                }
                startProgressTracking()
            }
        }
    }
    
    fun stopAudio() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
        }
        mediaPlayer = null
        currentAudioPath = null
        progressJob?.cancel()
        
        _state.update {
            it.copy(
                isPlaying = false,
                currentlyPlayingAudio = null,
                currentPosition = 0,
                duration = 0
            )
        }
    }
    
    fun togglePlayPause(audioFile: AudioFile) {
        val currentState = _state.value
        
        if (currentState.currentlyPlayingAudio?.path == audioFile.path) {
            // Same audio file - toggle play/pause
            if (currentState.isPlaying) {
                pauseAudio()
            } else {
                resumeAudio()
            }
        } else {
            // Different audio file - start playing this one
            playAudio(audioFile)
        }
    }
    
    fun clearError() {
        _state.update { it.copy(errorMessage = null, showOpenSettingsOption = false) }
    }
    
    fun openSystemSettings() {
        // This will be called from the UI to open system settings
        // The actual implementation will be in the UI layer
        _state.update { it.copy(showOpenSettingsOption = false) }
    }
    
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
    
    fun clearPermissionRequest() {
        _state.update { it.copy(showPermissionRequest = false, permissionType = "") }
    }
    
    fun getProgressPercentage(): Float {
        val currentState = _state.value
        return if (currentState.duration > 0) {
            currentState.currentPosition.toFloat() / currentState.duration.toFloat()
        } else {
            0f
        }
    }
    
    fun seekTo(position: Int) {
        mediaPlayer?.let { player ->
            if (position >= 0 && position <= player.duration) {
                player.seekTo(position)
                _state.update {
                    it.copy(currentPosition = position)
                }
            }
        }
    }
    
    fun seekToPercentage(percentage: Float) {
        val currentState = _state.value
        val duration = currentState.duration
        if (duration > 0) {
            val position = (percentage * duration).toInt()
            seekTo(position)
        }
    }
    
    fun setAsRingtone(audioFile: AudioFile, context: Context) {
        viewModelScope.launch {
            try {
                // Check permissions first
                if (!checkRingtonePermissions(context)) {
                    requestRingtonePermissions(context, "ringtone")
                    return@launch
                }
                
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                
                val file = File(audioFile.path)
                if (!file.exists()) {
                    _state.update {
                        it.copy(
                            errorMessage = "Audio file not found: ${audioFile.path}",
                            isLoading = false
                        )
                    }
                    return@launch
                }
                
                // Try to set as ringtone programmatically
                try {
                    // Use the original file directly instead of copying
                    val ringtoneUri = Uri.fromFile(file)
                    
                    // Set as default ringtone
                    val success = setDefaultRingtone(context, ringtoneUri)
                    
                    if (success) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Ringtone set successfully as default!\n\nYour new ringtone is now active and will play for incoming calls.",
                                showOpenSettingsOption = false
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Ringtone added to system but couldn't set as default.\n\nClick 'Open Settings' to set it manually as your default ringtone.",
                                showOpenSettingsOption = true
                            )
                        }
                    }
                    
                } catch (e: Exception) {
                    // If programmatic setting fails, guide user to manual setting
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "To set as ringtone:\n1. Go to Settings > Sound > Ringtone\n2. Select 'Add ringtone'\n3. Choose this audio file\n\nClick 'Open Settings' below to go directly to sound settings.",
                            showOpenSettingsOption = true
                        )
                    }
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        errorMessage = "Failed to set ringtone: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun setDefaultRingtone(context: Context, ringtoneUri: Uri): Boolean {
        return try {
            // Try to set as default ringtone using Settings.System
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    // Use Settings.System to set the ringtone URI
                    Settings.System.putString(context.contentResolver, Settings.System.RINGTONE, ringtoneUri.toString())
                    return true
                }
            }
            
            // Fallback: Try to verify the ringtone is accessible
            try {
                val ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
                ringtone != null
            } catch (e: Exception) {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun setAsNotification(audioFile: AudioFile, context: Context) {
        viewModelScope.launch {
            try {
                // Check permissions first
                if (!checkRingtonePermissions(context)) {
                    requestRingtonePermissions(context, "notification")
                    return@launch
                }
                
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                
                val file = File(audioFile.path)
                if (!file.exists()) {
                    _state.update {
                        it.copy(
                            errorMessage = "Audio file not found",
                            isLoading = false
                        )
                    }
                    return@launch
                }
                
                // Try to set as notification sound
                try {
                    // Use the original file directly instead of copying
                    val notificationUri = Uri.fromFile(file)
                    
                    // Set as default notification sound
                    val success = setDefaultNotificationSound(context, notificationUri)
                    
                    if (success) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Notification sound set successfully as default!\n\nYour new notification sound is now active and will play for notifications.",
                                showOpenSettingsOption = false
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Notification sound added to system but couldn't set as default.\n\nClick 'Open Settings' to set it manually as your default notification sound.",
                                showOpenSettingsOption = true
                            )
                        }
                    }
                    
                } catch (e: Exception) {
                    // If programmatic setting fails, guide user to manual setting
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "To set as notification sound:\n1. Go to Settings > Sound > Default notification sound\n2. Select 'Add sound'\n3. Choose this audio file\n\nClick 'Open Settings' below to go directly to sound settings.",
                            showOpenSettingsOption = true
                        )
                    }
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        errorMessage = "Failed to set notification sound: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun setDefaultNotificationSound(context: Context, notificationUri: Uri): Boolean {
        return try {
            // Try to set as default notification sound using Settings.System
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    // Use Settings.System to set the notification sound URI
                    Settings.System.putString(context.contentResolver, Settings.System.NOTIFICATION_SOUND, notificationUri.toString())
                    return true
                }
            }
            
            // Fallback: Try to verify the notification sound is accessible
            try {
                val ringtone = RingtoneManager.getRingtone(context, notificationUri)
                ringtone != null
            } catch (e: Exception) {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun setAsAlarm(audioFile: AudioFile, context: Context) {
        viewModelScope.launch {
            try {
                // Check permissions first
                if (!checkRingtonePermissions(context)) {
                    requestRingtonePermissions(context, "alarm")
                    return@launch
                }
                
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                
                val file = File(audioFile.path)
                if (!file.exists()) {
                    _state.update {
                        it.copy(
                            errorMessage = "Audio file not found",
                            isLoading = false
                        )
                    }
                    return@launch
                }
                
                // Try to set as alarm sound
                try {
                    // Use the original file directly instead of copying
                    val alarmUri = Uri.fromFile(file)
                    
                    // Set as default alarm sound
                    val success = setDefaultAlarmSound(context, alarmUri)
                    
                    if (success) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Alarm sound set successfully as default!\n\nYour new alarm sound is now active and will play for alarms.",
                                showOpenSettingsOption = false
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Alarm sound added to system but couldn't set as default.\n\nClick 'Open Settings' to set it manually as your default alarm sound.",
                                showOpenSettingsOption = true
                            )
                        }
                    }
                    
                } catch (e: Exception) {
                    // If programmatic setting fails, guide user to manual setting
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "To set as alarm sound:\n1. Go to Settings > Sound > Alarm sound\n2. Select 'Add sound'\n3. Choose this audio file\n\nClick 'Open Settings' below to go directly to sound settings.",
                            showOpenSettingsOption = true
                        )
                    }
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        errorMessage = "Failed to set alarm sound: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun setDefaultAlarmSound(context: Context, alarmUri: Uri): Boolean {
        return try {
            // Try to set as default alarm sound using Settings.System
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    // Use Settings.System to set the alarm sound URI
                    Settings.System.putString(context.contentResolver, Settings.System.ALARM_ALERT, alarmUri.toString())
                    return true
                }
            }
            
            // Fallback: Try to verify the alarm sound is accessible
            try {
                val ringtone = RingtoneManager.getRingtone(context, alarmUri)
                ringtone != null
            } catch (e: Exception) {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }
}