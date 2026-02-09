package com.example.videotoaudioconverter.presentation.audio_player_screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.media3.common.util.UnstableApi
import com.example.lifelinepro.presentation.comman.formatDurationAudio
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.player.AudioController
import com.example.videotoaudioconverter.presentation.audio_player_screen.component.AudioFile
import com.example.videotoaudioconverter.presentation.audio_player_screen.component.getAllAudioFiles
import com.example.videotoaudioconverter.presentation.success_screen.formatDurationAudio
import com.example.videotoaudioconverter.service.AudioPlayerService
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

//import android.Manifest
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.provider.Settings
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.LinearProgressIndicator
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.core.app.ActivityCompat
//import androidx.media3.common.util.UnstableApi
//import com.example.lifelinepro.presentation.comman.formatDurationAudio
//import com.example.videotoaudioconverter.R
//import com.example.videotoaudioconverter.player.AudioController
//import com.example.videotoaudioconverter.presentation.audio_player_screen.component.AudioFile
//import com.example.videotoaudioconverter.presentation.audio_player_screen.component.getAllAudioFiles
//import com.example.videotoaudioconverter.ui.theme.MyColors
//import ir.kaaveh.sdpcompose.sdp
//import ir.kaaveh.sdpcompose.ssp
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.withContext
//import org.koin.androidx.compose.koinViewModel

//@androidx.annotation.OptIn(UnstableApi::class)
//@Composable
//fun AudioPlayerScreen(
//    navigateBack: () -> Unit,
//    isSetRingtone: Boolean = false,
//    ringtoneType: String = "",
//    isAudioCutter: Boolean = false,
//    cutterType: String = "",
//    onAudioSelected: ((String, String) -> Unit)? = null,
//    viewModel: AudioPlayerViewModel = koinViewModel()
//) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val appContext = context.applicationContext
//    val state by viewModel.state.collectAsState()
//
//    // Permission Launcher for Android 13+ Notifications
//    val notificationLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted -> }
//
//
//    LaunchedEffect(Unit) {
//        val activity = context as? Activity
//        activity?.let {
//            withContext(Dispatchers.IO) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), 100)
//                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            } else {
//                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
//            }
//        }
//
//        withContext(Dispatchers.IO) {
//            viewModel.updateAllAudioFilter(getAllAudioFiles(context = context))
//        }
//    }
//        }
//
//    // Error Dialog
//    state.errorMessage?.let { errorMessage ->
//        AlertDialog(
//            onDismissRequest = { viewModel.clearError() },
//            title = { Text("Notice") },
//            text = { Text(errorMessage) },
//            confirmButton = {
//                Button(onClick = { viewModel.clearError() }) { Text("OK") }
//            }
//        )
//    }
//
//    // System Settings Permission Dialog
//    if (state.showPermissionRequest) {
//        AlertDialog(
//            onDismissRequest = { viewModel.clearPermissionRequest() },
//            title = { Text("Permission Required") },
//            text = { Text("To set ${state.permissionType.lowercase()} sounds, please allow the app to modify system settings.") },
//            confirmButton = {
//                Button(onClick = {
//                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
//                        data = Uri.parse("package:${context.packageName}")
//                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    }
//                    context.startActivity(intent)
//                    viewModel.clearPermissionRequest()
//                }) { Text("Grant") }
//            },
//            dismissButton = {
//                Button(onClick = { viewModel.clearPermissionRequest() }) { Text("Cancel") }
//            }
//        )
//    }
//
//    Scaffold(
//        containerColor = Color.White,
//        topBar = {
//            // Ensure this Composable exists in your project
//            AudioPlayerTopBar(
//                title = if (isAudioCutter) "Select Audio" else "Audio Player",
//                navigateBack = navigateBack,
//                onSearchChange = { viewModel.onSearchChange(it) },
//                searchIconClicked = { viewModel.searchIconClicked() },
//                crossIconClicked = { viewModel.crossIconClicked() },
//                searchText = state.searchText,
//                isSearchMode = !state.IdealTopBar,
//                searchResultsCount = state.filteredAudioFiles.size
//            )
//        },
//        bottomBar = {
//            state.currentlyPlayingAudio?.let { audio ->
//                MiniPlayer(
//                    audio = audio,
//                    isPlaying = state.isPlaying,
//                    currentPosition = state.currentPosition,
//                    duration = state.duration,
//                    onTogglePlay = {
//                        scope.launch(Dispatchers.IO) {
//                            viewModel.togglePlayPause(audio)
//                        }
//                    }
//                )
//            }
//        }
//    ) { paddingValues ->
//        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color.White)) {
//            if (state.isLoading) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MyColors.MainColor)
//            } else {
//                LazyColumn(modifier = Modifier.fillMaxSize()) {
//                    items(state.filteredAudioFiles) { audioFile ->
//                        AudioPlayerListItem(
//                            audioFile = audioFile,
//                            isCurrentlyPlaying = state.currentlyPlayingAudio?.uri == audioFile.uri,
//                            isPlaying = state.isPlaying && state.currentlyPlayingAudio?.uri == audioFile.uri,
//                            onItemClick = {
//                                if (isAudioCutter) {
//                                    onAudioSelected?.invoke(audioFile.uri.toString(),audioFile.title)
//                                } else {
//                                    scope.launch(Dispatchers.IO) {
//                                        val serviceIntent =
//                                            Intent(context, AudioPlayerService::class.java).apply {
//                                                putExtra("AUDIO_URI", audioFile.uri.toString())
//                                                putExtra(
//                                                    "TITLE",
//                                                    audioFile.title
//                                                )  // ✅ Add metadata
//                                                putExtra("ARTIST", audioFile.artist)
//                                            }
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                            context.startForegroundService(serviceIntent)
//                                        } else {
//                                            context.startService(serviceIntent)
//                                        }
//                                    }
//                                    viewModel.togglePlayPause(audioFile)
//                                }
//                            },
////                            isCurrentlyPlaying = state.currentlyPlayingAudio?.path == audioFile.path,
////                            isPlaying = state.isPlaying && state.currentlyPlayingAudio?.path == audioFile.path,
////                            onItemClick = {
////                                if (isAudioCutter) {
////                                    onAudioSelected?.invoke(audioFile.path, audioFile.title)
////                                } else {
////                                    viewModel.togglePlayPause(audioFile)
////                                }
////                            },
//                            onPlayPauseClick = {
//                                scope.launch(Dispatchers.IO) {
//                                    viewModel.togglePlayPause(audioFile)
//                                }
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AudioPlayerListItem(
//    audioFile: AudioFile,
//    isCurrentlyPlaying: Boolean,
//    isPlaying: Boolean,
//    onItemClick: () -> Unit,
//    onPlayPauseClick: () -> Unit
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth().clickable { onItemClick() }.padding(12.sdp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Box(
//            modifier = Modifier.size(32.sdp).clickable { onPlayPauseClick() },
//            contentAlignment = Alignment.Center
//        ) {
//            Image(
//                painter = painterResource(
//                    if (isCurrentlyPlaying && isPlaying) R.drawable.ic_pause_audio else R.drawable.ic_play_audio
//                ),
//                contentDescription = null,
//                modifier = Modifier.size(28.sdp)
//            )
//        }
//        Spacer(modifier = Modifier.width(12.sdp))
//        Column(modifier = Modifier.weight(1f)) {
//            Text(
//                text = audioFile.title,
//                fontSize = 14.ssp,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                color = if (isCurrentlyPlaying) MyColors.MainColor else Color.Black
//            )
//            Text(text = audioFile.artist, fontSize = 11.ssp, color = Color.Gray)
//        }
//        Text(text = formatDurationAudio(audioFile.duration.toLong()), fontSize = 10.ssp, color = Color.LightGray)
//    }
//}
//
//@Composable
//fun MiniPlayer(
//    audio: AudioFile,
//    isPlaying: Boolean,
//    currentPosition: Int,
//    duration: Int,
//    onTogglePlay: () -> Unit
//) {
//    val progress = if (duration > 0) currentPosition.toFloat() / duration else 0f
//    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F8F8)).padding(8.sdp)) {
//        LinearProgressIndicator(
//            progress = { progress },
//            modifier = Modifier.fillMaxWidth().height(2.dp),
//            color = MyColors.MainColor,
//            trackColor = Color.LightGray
//        )
//        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.sdp)) {
//            Text(text = audio.title, modifier = Modifier.weight(1f), maxLines = 1, fontSize = 13.ssp)
//            IconButton(onClick = onTogglePlay) {
//                Icon(
//                    painter = painterResource(if (isPlaying) R.drawable.ic_pause_audio else R.drawable.ic_play_audio),
//                    contentDescription = null,
//                    tint = MyColors.MainColor
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AudioPlayerTopBar(
//    title: String,
//    navigateBack: () -> Unit,
//    onSearchChange: (String) -> Unit,
//    searchIconClicked: () -> Unit,
//    crossIconClicked: () -> Unit,
//    searchText: String,
//    isSearchMode: Boolean,
//    searchResultsCount: Int
//) {
//        TopAppBar(
//            title = {
//                if (isSearchMode) {
//                    TextField(
//                        value = searchText,
//                        onValueChange = onSearchChange,
//                        placeholder = { Text("Search music...", fontSize = 14.ssp) },
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = TextFieldDefaults.colors(
//                            focusedContainerColor = Color.Transparent,
//                            unfocusedContainerColor = Color.Transparent,
//                            focusedIndicatorColor = Color.Transparent,
//                            unfocusedIndicatorColor = Color.Transparent
//                        )
//                    )
//                } else {
//                    Column {
//                        Text(text = title, fontSize = 18.ssp)
//                        if (searchResultsCount > 0) {
//                            Text(
//                                text = "$searchResultsCount songs",
//                                fontSize = 11.ssp,
//                                color = Color.Gray
//                            )
//                        }
//                    }
//                }
//            },
//            navigationIcon = {
//                IconButton(onClick = if (isSearchMode) crossIconClicked else navigateBack) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_back_arrow),
//                        contentDescription = "Back"
//                    )
//                }
//            },
//            actions = {
//                if (!isSearchMode) {
//                    IconButton(onClick = searchIconClicked) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_search),
//                            contentDescription = "Search"
//                        )
//                    }
//                }
//            },
//            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
//        )
//}


@Composable
fun AudioPlayerScreen(
    navigateBack: () -> Unit,
    isSetRingtone: Boolean = false,
    ringtoneType: String = "",
    isAudioCutter: Boolean = false,
    cutterType: String = "",
    onAudioSelected: ((String, String) -> Unit)? = null,
    viewModel: AudioPlayerViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val isPermissionGranted by remember {mutableStateOf(false)}
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle if needed
    }

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
                isAudioCutter -> "Select Audio"
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
                                isAudioCutter = isAudioCutter,
                                cutterType = cutterType,
                                viewModel = viewModel,
                                onPlayPauseClick = { viewModel.togglePlayPause(audioFile) },
                                context=context,
                                onSetRingtone = { audioFile -> viewModel.setAsRingtone(context = context, audioFile =  audioFile)
                                },
                                onAudioSelected = onAudioSelected
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
    isAudioCutter: Boolean = false,
    cutterType: String = "",
    viewModel: AudioPlayerViewModel,
    onPlayPauseClick: () -> Unit,
    onSetRingtone: (AudioFile) -> Unit,
    onAudioSelected: ((String, String) -> Unit)? = null,
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
            if (isAudioCutter) {
                // Audio Cutter button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    Box(
//                        modifier = Modifier
//                            .size(26.sdp)
//                            .clickable {
//                                onAudioSelected?.invoke(audioFile.path, audioFile.title)
//                            },
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Image(
//                            painter = painterResource(R.drawable.ic_audio_cutter),
//                            contentDescription = "Select for Audio Cutting"
//                        )
//                    }

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
                            modifier = Modifier.size(24.sdp)
                        )
                    }
                }
            } else if (isSetRingtone) {
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

            Row (modifier = Modifier.clickable(){
                if(isAudioCutter){
                    onAudioSelected?.invoke(audioFile.path, audioFile.title)
                }
            }){


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
        }

        // Additional options for audio cutter mode
//        if (isAudioCutter) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.sdp)
//            ) {
//                // Main action button for audio cutter
//                androidx.compose.material3.Button(
//                    onClick = {
//
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
//                        containerColor = MyColors.MainColor
//                    )
//                ) {
//                    Text(
//                        text = "Select for Audio Cutting",
//                        color = Color.White,
//                        fontSize = 14.ssp
//                    )
//                }
//            }
//        }

        // Additional options for set ringtone mode
        if (isSetRingtone) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.sdp)
            ) {
                // Main action button based on ringtone type
                Button(
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
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.sdp),
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
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 0.sdp),
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
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.sdp),
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
            .padding(top = 15.sdp, start = 18.sdp, end = 18.sdp)
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

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PlayTestButton() {
    val context = LocalContext.current

    Button(onClick = {
        AudioController.play(
            context,
            "file:///storage/emulated/0/Music/test.mp3"
        )
    }) {
        Text("Play Audio")
    }
}

@UnstableApi
@Composable
fun AudioPlayerTestScreen(audioList: List<Pair<String, String>>) {
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        audioList.forEachIndexed { index, audio ->
            Button(
                onClick = {
                    AudioController.ensureServiceStarted(context)
                    //AudioController.playPlaylist(context, audioList, index)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = audio.second) // song title
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { AudioController.previous() }) {
                Text("Prev")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { AudioController.pause() }) {
                Text("Pause")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { AudioController.resume() }) {
                Text("Play")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { AudioController.next() }) {
                Text("Next")
            }
        }
    }
}