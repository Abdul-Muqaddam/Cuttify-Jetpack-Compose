package com.example.videotoaudioconverter.presentation.audio_player_screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.all_video_files.components.getVideoDuration
import com.example.videotoaudioconverter.presentation.audio_player_screen.component.AudioFile
import com.example.videotoaudioconverter.presentation.audio_player_screen.component.getAllAudioFiles
import com.example.videotoaudioconverter.presentation.video_to_audio_converter.component.TopBar
import com.example.videotoaudioconverter.ui.theme.MyColors
import com.example.lifelinepro.presentation.comman.formatDurationAudio
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import android.content.Intent
import android.provider.Settings
import kotlinx.coroutines.delay

@Composable
fun AudioPlayerScreen(
    navigateBack: () -> Unit, 
    isSetRingtone: Boolean = false,
    ringtoneType: String = "",
    viewModel: AudioPlayerViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val isPermissionGranted by remember {mutableStateOf(false)}
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val activity = context as? Activity
            activity?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                        100
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        100
                    )
                }
            }
            
            // Check ringtone permissions if we're in set ringtone mode
            if (isSetRingtone) {
                viewModel.checkRingtonePermissions(context)
            }
            
            viewModel.updateAllAudioFilter(getAllAudioFiles(context = context))
        }
    }
    
    // Monitor permission changes when user returns from settings
    LaunchedEffect(state.showPermissionRequest) {
        if (!state.showPermissionRequest && isSetRingtone) {
            // User might have returned from settings, refresh permissions
            delay(500) // Small delay to ensure settings have been applied
            viewModel.refreshPermissions(context)
        }
    }
    
    // Show error dialog if there's an error
    state.errorMessage?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { 
                Text(
                    when (ringtoneType.lowercase()) {
                        "ringtone" -> "Set Ringtone"
                        "notification" -> "Set Notification Sound"
                        "alarm" -> "Set Alarm Sound"
                        else -> "Set Ringtone"
                    }
                ) 
            },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            },
            dismissButton = if (state.showOpenSettingsOption) {
                {
                    Button(
                        onClick = { 
                            try {
                                // Try to open sound settings first
                                val soundIntent = Intent(Settings.ACTION_SOUND_SETTINGS)
                                soundIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(soundIntent)
                                viewModel.openSystemSettings()
                            } catch (e: Exception) {
                                // Fallback to general settings
                                try {
                                    val generalIntent = Intent(Settings.ACTION_SETTINGS)
                                    generalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(generalIntent)
                                    viewModel.openSystemSettings()
                                } catch (e2: Exception) {
                                    // Show error if both fail
                                    viewModel.clearError()
                                    // You could show a toast here
                                }
                            }
                        }
                    ) {
                        Text("Open Settings")
                    }
                }
            } else null
        )
    }
    
    // Show permission request dialog if needed
    if (state.showPermissionRequest) {
        AlertDialog(
            onDismissRequest = { viewModel.clearPermissionRequest() },
            title = { 
                Text("Permission Required") 
            },
            text = { 
                Text(
                    "To set ${state.permissionType.lowercase()} sounds, this app needs permission to modify system settings.\n\n" +
                    "Steps to grant permission:\n" +
                    "1. Click 'Grant Permission' below\n" +
                    "2. Find 'Cuttify' in the app list\n" +
                    "3. Toggle 'Allow modify system settings'\n" +
                    "4. Return to this app\n\n" +
                    "This permission is required for Android 6.0+ devices."
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.openPermissionSettings(context) }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.clearPermissionRequest() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(containerColor = Color.White, topBar = {
        AudioPlayerTopBar(
            title = when {
                isSetRingtone && ringtoneType.isNotEmpty() -> "Select Audio for ${ringtoneType.capitalize()}"
                isSetRingtone -> stringResource(R.string.set_ringtone)
                else -> stringResource(R.string.select_audio)
            }, 
            navigateBack = { navigateBack() },
            onSearchChange = { viewModel.onSearchChange(it) },
            searchIconClicked = { viewModel.searchIconClicked() },
            crossIconClicked = { viewModel.crossIconClicked() },
            searchText = state.searchText,
            isSearchMode = !state.IdealTopBar,
            searchResultsCount = state.filteredAudioFiles.size
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(color = Color.White)
                .statusBarsPadding()
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MyColors.MainColor)
                }
            } else {
                if (state.filteredAudioFiles.isEmpty() && state.searchText.isNotEmpty()) {
                    // No search results found
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No audio files found",
                                fontSize = 18.ssp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Try a different search term",
                                fontSize = 14.ssp,
                                color = Color.LightGray,
                                modifier = Modifier.padding(top = 8.sdp)
                            )
                        }
                    }
                } else {
                    LazyColumn {
                        items(state.filteredAudioFiles) { audioFile ->
                            AudioPlayerComponent(
                                audioFile = audioFile,
                                isCurrentlyPlaying = state.currentlyPlayingAudio?.path == audioFile.path,
                                isPlaying = state.isPlaying && state.currentlyPlayingAudio?.path == audioFile.path,
                                isSetRingtone = isSetRingtone,
                                ringtoneType = ringtoneType,
                                viewModel = viewModel,
                                onPlayPauseClick = { viewModel.togglePlayPause(audioFile) },
                                context=context,
                                onSetRingtone = { audioFile -> viewModel.setAsRingtone(context = context, audioFile =  audioFile)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioPlayerComponent(
    audioFile: AudioFile,
    isCurrentlyPlaying: Boolean,
    isPlaying: Boolean,
    isSetRingtone: Boolean,
    ringtoneType: String,
    viewModel: AudioPlayerViewModel,
    onPlayPauseClick: () -> Unit,
    onSetRingtone: (AudioFile) -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.sdp, horizontal = 15.sdp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSetRingtone) {
                // Set Ringtone button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.sdp)
                            .clickable { onSetRingtone(audioFile) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_ringtoon),
                            contentDescription = "Set as Ringtone"
                        )
                    }
                    
                    // Small preview button
                    Box(
                        modifier = Modifier
                            .size(20.sdp)
                            .padding(top = 4.sdp)
                            .clickable { onPlayPauseClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                if (isCurrentlyPlaying && isPlaying) R.drawable.ic_pause_audio 
                                else R.drawable.ic_play_audio
                            ),
                            contentDescription = if (isCurrentlyPlaying && isPlaying) "Pause" else "Preview",
                            modifier = Modifier.size(16.sdp)
                        )
                    }
                }
            } else {
                // Play/Pause button with visual feedback
                Box(
                    modifier = Modifier
                        .size(26.sdp)
                        .clickable { onPlayPauseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            if (isCurrentlyPlaying && isPlaying) R.drawable.ic_pause_audio 
                            else R.drawable.ic_play_audio
                        ),
                        contentDescription = if (isCurrentlyPlaying && isPlaying) "Pause" else "Play"
                    )
                    
                    // Show playing indicator
                    if (isCurrentlyPlaying && isPlaying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.dp,
                            color = MyColors.MainColor
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 15.sdp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_wave_temp), 
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    color = MyColors.MainColor, 
                    text = audioFile.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Text(
                fontSize = 16.ssp, 
                color = MyColors.MainColor, 
                text = formatDurationAudio(audioFile.duration)
            )
        }
        
        // Additional options for set ringtone mode
        if (isSetRingtone) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.sdp)
            ) {
                // Main action button based on ringtone type
                androidx.compose.material3.Button(
                    onClick = { 
                        when (ringtoneType.lowercase()) {
                            "ringtone" -> viewModel.setAsRingtone(context = context, audioFile =  audioFile)
                            "notification" -> viewModel.setAsNotification(audioFile, context)
                            "alarm" -> viewModel.setAsAlarm(audioFile, context)
                            else -> viewModel.setAsRingtone(audioFile, context)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MyColors.MainColor
                    )
                ) {
                    Text(
                        text = when (ringtoneType.lowercase()) {
                            "ringtone" -> "Set as Ringtone"
                            "notification" -> "Set as Notification"
                            "alarm" -> "Set as Alarm"
                            else -> "Set as Ringtone"
                        },
                        color = Color.White,
                        fontSize = 14.ssp
                    )
                }
                
                // Additional options for all ringtone types
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.sdp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                ) {
                    // Set as Ringtone
                    androidx.compose.material3.Button(
                        onClick = { 
                            viewModel.setAsRingtone(audioFile, context) 
                        },
                        modifier = Modifier.weight(1f).padding(end = 4.sdp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MyColors.MainColor
                        )
                    ) {
                        Text(
                            text = "Ringtone",
                            color = Color.White,
                            fontSize = 10.ssp
                        )
                    }
                    
                    // Set as Notification
                    androidx.compose.material3.Button(
                        onClick = { 
                            viewModel.setAsNotification(audioFile, context) 
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 0.sdp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MyColors.MainColor
                        )
                    ) {
                        Text(
                            text = "Notification",
                            color = Color.White,
                            fontSize = 9.ssp
                        )
                    }
                    
                    // Set as Alarm
                    androidx.compose.material3.Button(
                        onClick = { 
                            viewModel.setAsAlarm(audioFile, context) 
                        },
                        modifier = Modifier.weight(1f).padding(start = 4.sdp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MyColors.MainColor
                        )
                    ) {
                        Text(
                            text = "Alarm",
                            color = Color.White,
                            fontSize = 10.ssp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioPlayerTopBar(
    title: String,
    navigateBack: () -> Unit,
    onSearchChange: (String) -> Unit,
    searchIconClicked: () -> Unit,
    crossIconClicked: () -> Unit,
    searchText: String,
    isSearchMode: Boolean,
    searchResultsCount: Int = 0
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.sdp, horizontal = 18.sdp)
            .statusBarsPadding(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .size(20.sdp)
                    .clickable { navigateBack() },
                painter = painterResource(R.drawable.ic_back_arrow),
                contentDescription = null
            )
            
            if (!isSearchMode) {
                Text(
                    modifier = Modifier.padding(start = 15.sdp),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    fontSize = 20.ssp,
                    text = title,
                    color = MyColors.MainColor
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 15.sdp)
                        .fillMaxWidth()
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.ssp
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        value = searchText,
                        onValueChange = { onSearchChange(it) }
                    )
                    
                    // Search icon indicator
                    Image(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.sdp)
                            .align(Alignment.CenterEnd),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray)
                    )
                    
                    // Placeholder text
                    if (searchText.isEmpty()) {
                        Text(
                            fontSize = 16.ssp,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(bottom = 3.sdp),
                            text = stringResource(R.string.search),
                            color = Color.Gray
                        )
                    } else {
                        // Show search results count
                        Text(
                            fontSize = 12.ssp,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(top = 20.sdp),
                            text = "$searchResultsCount audio files found",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        
        Image(
            modifier = Modifier
                .size(20.sdp)
                .clickable {
                    if (!isSearchMode) {
                        searchIconClicked()
                    } else {
                        crossIconClicked()
                    }
                },
            painter = if (!isSearchMode) {
                painterResource(R.drawable.ic_search)
            } else {
                painterResource(R.drawable.ic_cross)
            },
            contentDescription = null
        )
    }
}