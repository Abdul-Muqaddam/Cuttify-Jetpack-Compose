package com.example.videotoaudioconverter.presentation.videoCutterScreen

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.service.VideoTrimmerService
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

private val BgDark = Color(0xFFFFFFFF)
private val BgCard = Color(0xFFF5F5F5)
private val Purple = Color(0xFF2E7D32)
private val PurpleLight = Color(0xFF66BB6A)
private val RedAccent = Color(0xFFFF6B6B)
private val TextSecondary = Color(0xFF757575)
private val HandleColor = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCutterScreen(
    videoUri: Uri,
    navigateToSuccessScreen: (String) -> Unit,
    navigateBackToHomeScreen: () -> Unit,
    viewModel: VideoCutterViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var sliderWidthPx by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    // ExoPlayer setup
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(state.trimmedFile) {
        state.trimmedFile?.let { file ->
            // Navigate to SuccessScreen
            navigateToSuccessScreen(file.absolutePath)// optional, depends on your flow
            viewModel.clearTrimmedFile() // clear the state
            // Replace with actual navigation
            // Example using AppDestination (if passed as parameter):
            // appDestination.navigateToSuccessScreen(fileName, filePath)
        }
    }

    LaunchedEffect(videoUri) {
        val mediaItem = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.seekTo(state.startMs)
    }

    LaunchedEffect(videoUri) {
        viewModel.loadVideo(videoUri)
    }

    // Track playback position
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(100)
            val pos = exoPlayer.currentPosition
            viewModel.updateCurrentPosition(pos)
            if (pos >= state.endMs) {
                exoPlayer.seekTo(state.startMs)
                exoPlayer.pause()
                isPlaying = false
            }
        }
    }

    // Cleanup player
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }


    // ── Dialogs ──────────────────────────────────────────────────


    // Error Dialog
    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            containerColor = BgCard,
            icon = {
                Icon(Icons.Default.Error, null, tint = RedAccent, modifier = Modifier.size(48.dp))
            },
            title = { Text("Something went wrong", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(state.errorMessage!!, color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearError() },
                    colors = ButtonDefaults.buttonColors(containerColor = RedAccent),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("OK") }
            }
        )
    }

    // Trimming Progress Dialog
    if (state.isTrimming) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = BgCard,
            icon = {
                Icon(Icons.Default.ContentCut, null, tint = Purple, modifier = Modifier.size(48.dp))
            },
            title = {
                Text("Trimming Video...", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { state.trimProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Purple,
                        trackColor = Color(0xFF3A3A5C)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "${state.trimProgress}%",
                        color = PurpleLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Please wait...", color = TextSecondary, fontSize = 13.sp)
                }
            },
            confirmButton = {}
        )
    }

    // ── Main UI ──────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Video Cutter Screen",
                        fontSize = 22.ssp,
                        color = MyColors.Green058,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                        Image(
                            painter = painterResource(R.drawable.ic_baseline_arrow_back),
                            contentDescription = null,
                            modifier = Modifier
                                .height(22.sdp)
                                .width(22.sdp)
                                .clickable { navigateBackToHomeScreen() }
                        )

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BgDark
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

//            if (state.videoUri == null) {
//                // ── Empty State ───────────────────────────────────
//                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.padding(32.dp)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .size(130.dp)
//                                .clip(CircleShape)
//                                .background(
//                                    Brush.radialGradient(listOf(Purple, Color(0xFF007250)))
//                                ),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                Icons.Default.VideoLibrary,
//                                null,
//                                tint = Color.White,
//                                modifier = Modifier.size(56.dp)
//                            )
//                        }
//                        Spacer(Modifier.height(28.dp))
//                        Text(
//                            "Cut Your Video",
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 24.sp
//                        )
//                        Spacer(Modifier.height(10.dp))
//                        Text(
//                            "Select any video from your device\nand trim it to perfection",
//                            color = TextSecondary,
//                            textAlign = TextAlign.Center,
//                            fontSize = 14.sp
//                        )
//                        Spacer(Modifier.height(36.dp))
//                        Button(
//                            onClick = { launcher.launch("video/*") },
//                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
//                            shape = RoundedCornerShape(16.dp),
//                            modifier = Modifier
//                                .height(54.dp)
//                                .width(220.dp)
//                        ) {
//                            Icon(Icons.Default.Add, null)
//                            Spacer(Modifier.width(8.dp))
//                            Text(
//                                "Select Video",
//                                fontWeight = FontWeight.SemiBold,
//                                fontSize = 16.sp
//                            )
//                        }
//                    }
//                }
//
//            } else {
                // ── Video Player ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color.Black)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.height(12.dp))

                // ── Time Labels ───────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("START", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(formatTime(state.startMs), color = Purple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DURATION", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(formatTime(state.endMs - state.startMs), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("END", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(formatTime(state.endMs), color = RedAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Thumbnail Strip ───────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .onGloballyPositioned { sliderWidthPx = it.size.width.toFloat() }
                ) {
                    // Thumbnails row
                    if (state.isLoadingThumbnails) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color(0xFF2A2A3C)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Purple, modifier = Modifier.size(24.dp))
                        }
                    } else {
                        Row(Modifier.fillMaxSize()) {
                            state.thumbnails.forEach { bmp ->
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                            if (state.thumbnails.isEmpty()) {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF2A2A3C))
                                )
                            }
                        }
                    }

                    // Overlay dims + trim border
                    if (sliderWidthPx > 0 && state.durationMs > 0) {
                        val startFrac = state.startMs.toFloat() / state.durationMs
                        val endFrac = state.endMs.toFloat() / state.durationMs

                        // Left dim
                        if (startFrac > 0f) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(startFrac)
                                    .background(Color(0xAA000000))
                                    .align(Alignment.CenterStart)
                            )
                        }

                        // Right dim
                        if (endFrac < 1f) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(1f - endFrac)
                                    .background(Color(0xAA000000))
                                    .align(Alignment.CenterEnd)
                            )
                        }

                        // Purple border around selected region
                        val startOffsetDp = with(density) { (startFrac * sliderWidthPx).toDp() }
                        val endOffsetDp = with(density) { ((1f - endFrac) * sliderWidthPx).toDp() }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .padding(start = startOffsetDp, end = endOffsetDp)
                                .border(2.dp, Purple, RoundedCornerShape(4.dp))
                        )

                        // Playhead
                        val posFrac = if (state.durationMs > 0)
                            state.currentPositionMs.toFloat() / state.durationMs else 0f
                        val posOffsetDp = with(density) { (posFrac * sliderWidthPx).toDp() }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(2.dp)
                                .offset(x = posOffsetDp)
                                .background(Color.White)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                Text(
                    "Trim Range",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(8.dp))

                RangeSlider(
                    value = if (state.durationMs > 0)
                        state.startMs.toFloat()..state.endMs.toFloat()
                    else 0f..0f,

                    onValueChange = { range ->
                        val newStart = range.start.toLong()
                        val newEnd = range.endInclusive.toLong()

                        // Optional: enforce minimum trim duration
                        if (newEnd - newStart >= 1000) {
                            viewModel.updateStartMs(newStart)
                            viewModel.updateEndMs(newEnd)

                            exoPlayer.seekTo(newStart)
                            viewModel.updateCurrentPosition(newStart)
                        }
                    },

                    valueRange = 0f..state.durationMs.toFloat(),

                    modifier = Modifier.fillMaxWidth(),

                    colors = SliderDefaults.colors(
                        thumbColor = Purple,
                        activeTrackColor = Purple,
                        inactiveTrackColor = Color(0xFF3A3A5C)
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Optional: display start & end times
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(state.startMs), color = Purple, fontWeight = FontWeight.Bold)
                    Text(formatTime(state.endMs), color = RedAccent, fontWeight = FontWeight.Bold)
                }
            }

                Spacer(Modifier.height(16.dp))

                // ── Playback Controls ─────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Seek to start
                    IconButton(
                        onClick = {
                            exoPlayer.seekTo(state.startMs)
                            viewModel.updateCurrentPosition(state.startMs)
                        }
                    ) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    // Play / Pause
                    FilledIconButton(
                        onClick = {
                            if (isPlaying) {
                                exoPlayer.pause()
                                isPlaying = false
                            } else {
                                val pos = exoPlayer.currentPosition
                                if (pos >= state.endMs || pos < state.startMs) {
                                    exoPlayer.seekTo(state.startMs)
                                }
                                exoPlayer.play()
                                isPlaying = true
                            }
                        },
                        modifier = Modifier.size(60.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Purple)
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    // Seek to end
                    IconButton(
                        onClick = {
                            exoPlayer.seekTo(state.endMs)
                            viewModel.updateCurrentPosition(state.endMs)
                        }
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // ── Trim Button ───────────────────────────────────
                Button(
                    onClick = {
                        isPlaying = false
                        exoPlayer.pause()
                        viewModel.trimVideo(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !state.isTrimming && state.endMs > state.startMs
                ) {
                    Icon(Icons.Default.ContentCut, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Trim & Save Video",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }


// ── Helper ────────────────────────────────────────────────────────
fun formatTime(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    val millis = (ms % 1000) / 10
    return "%02d:%02d.%02d".format(minutes, seconds, millis)
}