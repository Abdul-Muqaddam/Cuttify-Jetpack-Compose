package com.example.videotoaudioconverter.presentation.audioMergeScreen


import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.videotoaudioconverter.ui.theme.MyColors
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.videotoaudioconverter.service.AudioMergeService
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioMergerScreen(
    onBackClick: () -> Unit,
    navigateToSuccess: (String, String) -> Unit,
    viewModel: AudioMergerViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var outputFileName by remember { mutableStateOf("merged_audio") }


    LaunchedEffect(viewModel) {
        viewModel.uiState.collectLatest { state ->
            when (val result = state.mergeResult) {
                is MergeResult.Success -> {
                    // Navigate to success screen with output path and file name
                    navigateToSuccess(
                        result.outputPath,
                        "Merged_Audio_${System.currentTimeMillis()}.mp3"
                    )

                    // Reset result to avoid re-triggering navigation
                    viewModel.resetResult()
                }

                is MergeResult.Error -> {
                    Log.e("AudioMergeScreen", "Merge failed: ${result.message}")
                }

                else -> Unit
            }
        }
    }

    // ExoPlayer state
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.addAudio(it) }
    }


    // Show dialogs
    if (uiState.mergeResult is MergeResult.Success) {
        LaunchedEffect(uiState.mergeResult) {
            player?.release()
            player = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri((uiState.mergeResult as MergeResult.Success).outputPath)
                setMediaItem(mediaItem)
                prepare()
            }
        }
    }

    if (uiState.mergeResult is MergeResult.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.resetResult() },
            confirmButton = {
                TextButton(onClick = { viewModel.resetResult() }) { Text("OK") }
            },
            title = { Text("❌ Merge Failed") },
            text = { Text((uiState.mergeResult as MergeResult.Error).message) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audio Merger", fontWeight = FontWeight.Bold, color = MyColors.MainColor) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MyColors.MainColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Output filename
            OutlinedTextField(
                value = outputFileName,
                onValueChange = { outputFileName = it },
                label = { Text("Output File Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Text(".mp3", color = Color.Black, modifier = Modifier.padding(end = 8.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    cursorColor = MyColors.green841,
                    focusedBorderColor = MyColors.Green058,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = MyColors.Green058,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(15.sdp))

            // Add audio
            OutlinedButton(
                onClick = { audioPickerLauncher.launch("audio/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.Add, contentDescription = null,tint = MyColors.MainColor)
                Spacer(Modifier.width(8.dp))
                Text("Add Audio File",color = MyColors.MainColor)
            }

            // Selected files count
            Text(
                text = "Selected Files (${uiState.selectedAudios.size})",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            // Audio file list
            if (uiState.selectedAudios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = MyColors.Green058, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No audio files added yet", color = Color.Black)
                        Text("Add at least 2 files to merge", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.selectedAudios) { uri ->
                        AudioMergerFileCard(uri = uri, onRemove = { viewModel.removeAudio(uri) })
                    }
                }
            }

            // Merge button
            Button(
                onClick = {

                    // 1️⃣ Start Foreground Service
                    val intent = Intent(context, AudioMergeService::class.java)
                    ContextCompat.startForegroundService(context, intent)

                    // 2️⃣ Call mergeAudios (your updated function already updates notification)
                    viewModel.mergeAudios(context, outputFileName)

                },
                enabled = !uiState.isMerging && uiState.selectedAudios.size >= 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MyColors.MainColor)
            ) {
                if (uiState.isMerging) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("Merging...", color = Color.White)
                } else {
                    Icon(Icons.Default.CallMerge, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Merge Audio Files", fontSize = 16.sp, color = Color.White)
                }
            }

//            Button(
//                onClick = { viewModel.mergeAudios(context, outputFileName) },
//                enabled = !uiState.isMerging && uiState.selectedAudios.size >= 2,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(54.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = MyColors.MainColor)
//            ) {
//                if (uiState.isMerging) {
//                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
//                    Spacer(Modifier.width(10.dp))
//                    Text("Merging...", color = Color.White)
//                } else {
//                    Icon(Icons.Default.CallMerge, contentDescription = null, tint = Color.White)
//                    Spacer(Modifier.width(8.dp))
//                    Text("Merge Audio Files", fontSize = 16.sp, color = Color.White)
//                }
//            }

            // Playback controls
            uiState.mergeResult.let { result ->
                if (result is MergeResult.Success) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Button(onClick = {
                            player?.let {
                                if (it.isPlaying) {
                                    it.pause()
                                    isPlaying = false
                                } else {
                                    it.play()
                                    isPlaying = true
                                }
                            }
                        }) {
                            Text(if (isPlaying) "Pause" else "Play")
                        }

                        Text(
                            text = "Merged Audio Ready",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Release player when leaving screen
    DisposableEffect(Unit) {
        onDispose { player?.release() }
    }
}

@Composable
fun AudioMergerFileCard(uri: Uri, onRemove: () -> Unit) {
    val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "Audio File"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AudioFile, contentDescription = null, tint = MyColors.MainColor, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Text(text = fileName, modifier = Modifier.weight(1f), maxLines = 2, fontSize = 14.sp, color = Color.Black)
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}


//import android.app.Application
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import ir.kaaveh.sdpcompose.sdp
//import ir.kaaveh.sdpcompose.ssp
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.videotoaudioconverter.ui.theme.MyColors
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AudioMergerScreen(
//    onBackClick: () -> Unit,
//    viewModel: AudioMergerViewModel = viewModel(
//        factory = ViewModelProvider.AndroidViewModelFactory(
//            LocalContext.current.applicationContext as Application
//        )
//    )
//) {
//    val context = LocalContext.current
//    val uiState by viewModel.uiState.collectAsState()
//    var outputFileName by remember { mutableStateOf("merged_audio") }
//
//    val audioPickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { viewModel.addAudio(it) }
//    }
//
//    if (uiState.mergeResult is MergeResult.Success) {
//        AlertDialog(
//            onDismissRequest = { viewModel.resetResult() },
//            confirmButton = {
//                TextButton(onClick = { viewModel.resetResult() }) {
//                    Text("OK")
//                }
//            },
//            title = { Text("✅ Merge Successful!") },
//            text = {
//                Text("Saved at:\nMusic/Cuttify/Merged Audio/$outputFileName.mp3")
//            }
//        )
//    }
//
//    if (uiState.mergeResult is MergeResult.Error) {
//        AlertDialog(
//            onDismissRequest = { viewModel.resetResult() },
//            confirmButton = {
//                TextButton(onClick = { viewModel.resetResult() }) {
//                    Text("OK")
//                }
//            },
//            title = { Text("❌ Merge Failed") },
//            text = { Text((uiState.mergeResult as MergeResult.Error).message) }
//        )
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Audio Merger", fontWeight = FontWeight.Bold, color = MyColors.MainColor) },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MyColors.MainColor)
//                    }
//                },
//            )
//        }
//    ) { paddingValues ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//
//
//            OutlinedTextField(
//                value = outputFileName,
//                onValueChange = { outputFileName = it },
//                label = { Text("Output File Name") },
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth(),
//                trailingIcon = {
//                    Text(
//                        ".mp3",
//                        color = Color.Gray,
//                        modifier = Modifier.padding(end = 8.dp)
//                    )
//                },
//                colors = OutlinedTextFieldDefaults.colors(
//                    cursorColor = MyColors.green841,
//                    focusedBorderColor = MyColors.Green058,
//                    focusedLabelColor = MyColors.Green058,
//                    )
//
//            )
//            Spacer(modifier = Modifier.height(15.sdp))
//            OutlinedButton(
//                onClick = { audioPickerLauncher.launch("audio/*") },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Icon(Icons.Default.Add, contentDescription = null)
//                Spacer(Modifier.width(8.dp))
//                Text("Add Audio File")
//            }
//
//            Text(
//                text = "Selected Files (${uiState.selectedAudios.size})",
//                fontWeight = FontWeight.SemiBold,
//                fontSize = 16.sp
//            )
//
//            if (uiState.selectedAudios.isEmpty()) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(140.dp)
//                        .background(
//                            MaterialTheme.colorScheme.surfaceVariant,
//                            RoundedCornerShape(12.dp)
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Icon(
//                            Icons.Default.LibraryMusic,
//                            contentDescription = null,
//                            tint = Color.Gray,
//                            modifier = Modifier.size(36.dp)
//                        )
//                        Spacer(Modifier.height(8.dp))
//                        Text("No audio files added yet", color = Color.Gray)
//                        Text("Add at least 2 files to merge", color = Color.Gray, fontSize = 12.sp)
//                    }
//                }
//            } else {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(uiState.selectedAudios) { uri ->
//                        AudioMergerFileCard(
//                            uri = uri,
//                            onRemove = { viewModel.removeAudio(uri) }
//                        )
//                    }
//                }
//            }
//
//            Button(
//                onClick = {
//                    viewModel.mergeAudios(context, outputFileName)
//                },
//                enabled = !uiState.isMerging && uiState.selectedAudios.size >= 2,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(54.dp),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                if (uiState.isMerging) {
//                    CircularProgressIndicator(
//                        color = Color.White,
//                        modifier = Modifier.size(22.dp),
//                        strokeWidth = 2.dp
//                    )
//                    Spacer(Modifier.width(10.dp))
//                    Text("Merging...")
//                } else {
//                    Icon(Icons.Default.CallMerge, contentDescription = null)
//                    Spacer(Modifier.width(8.dp))
//                    Text("Merge Audio Files", fontSize = 16.sp)
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun AudioMergerFileCard(uri: Uri, onRemove: () -> Unit) {
//    val fileName = uri.lastPathSegment
//        ?.substringAfterLast("/")
//        ?: "Audio File"
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(10.dp),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                Icons.Default.AudioFile,
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.primary,
//                modifier = Modifier.size(32.dp)
//            )
//            Spacer(Modifier.width(12.dp))
//            Text(
//                text = fileName,
//                modifier = Modifier.weight(1f),
//                maxLines = 2,
//                fontSize = 14.sp
//            )
//            IconButton(onClick = onRemove) {
//                Icon(
//                    Icons.Default.Close,
//                    contentDescription = "Remove",
//                    tint = Color.Red
//                )
//            }
//        }
//    }
//}