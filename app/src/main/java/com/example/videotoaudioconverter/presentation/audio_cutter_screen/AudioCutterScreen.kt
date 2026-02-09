package com.example.videotoaudioconverter.presentation.audio_cutter_screen

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.all_folder.VideoToAudioConverterViewModel
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.videotoaudioconverter.audio.AudioExporter
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AudioCutterScreen(
    audioPath: String,
    audioTitle: String,
    navigateBack: () -> Unit,
    navigateToSuccess: (String) -> Unit,
    viewModel: AudioCutterViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var fileName by remember { mutableStateOf(audioTitle) }

    LaunchedEffect(viewModel) {
        AudioTrimCallback.viewModel = viewModel
    }

    LaunchedEffect(audioPath) {
        if (audioPath.isNotEmpty()) {
            val audioUri = Uri.parse(audioPath)
            viewModel.load(context, audioUri)
        }
    }

    LaunchedEffect(viewModel.trimResult) {
        viewModel.trimResult.collectLatest { result ->
            when (result) {
                is AudioCutterViewModel.AudioTrimResult.Success -> {
                    // Navigate to Success screen with output path
                    navigateToSuccess(result.outputPath)
                }
                is AudioCutterViewModel.AudioTrimResult.Error -> {
                    // Optionally show a Snackbar or Toast
                    Log.e("AudioCutterScreen", "Trim failed: ${result.message}")
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = { AudioCutterTopBar(title = "Audio Cutter", navigateBack = navigateBack) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Rename",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium,
                color = MyColors.MainColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            BasicTextField(
                value = fileName,
                onValueChange = { fileName = it },
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MyColors.MainColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    if (fileName.isEmpty()) {
                        Text(
                            text = "File Name user can change it",
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = MyColors.MainColor.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            AudioCutterSlider(
                startFraction = state.startFraction,
                endFraction = state.endFraction,
                totalDuration = state.totalDuration,
                onStartFractionChange = {
                    viewModel.onStartFractionChanged(it)
                  //  viewModel.onSeekToFraction(it)
                },
                onEndFractionChange = { viewModel.onEndFractionChanged(it) },
                modifier = Modifier.fillMaxWidth().height(80.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.rewind5Sec() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_reverse_five_sec),
                        contentDescription = "Rewind",
                        tint = MyColors.MainColor
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                IconButton(onClick = { viewModel.onPlayPauseClicked() }) {
                    Icon(
                        painter = painterResource(
                            if (state.isPlaying) R.drawable.ic_pause_audio
                            else R.drawable.ic_play_audio
                        ),
                        contentDescription = null,
                        tint = MyColors.MainColor,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                IconButton(onClick = { viewModel.forward5Sec() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_skip_five_sec),
                        contentDescription = "Forward",
                        tint = MyColors.MainColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Export Button
            Button(
                onClick = {
                    val outputFileName = "${fileName}_${System.currentTimeMillis()}.mp3"
                    viewModel.exportAudio(outputFileName)
                },
//                onClick = {
//
//                    if (viewModel.getAudioPath().isNotEmpty()) {
//                        // Trigger the export function in ViewModel
//                        viewModel.exportAudio("trimmed_audio_${System.currentTimeMillis()}.mp3")
//                    } else {
//                        Log.d("AudioCutter", "Audio path is empty, cannot export")
//                    }
//
//                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MyColors.MainColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.isExporting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Cut Audio",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AudioCutterTopBar(title: String, navigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = navigateBack) {
            Icon(
                painter = painterResource(R.drawable.ic_back_arrow),
                contentDescription = "Back",
                tint = MyColors.MainColor
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = title,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MyColors.MainColor
        )
    }
}

@Composable
fun AudioCutterSlider(
    startFraction: Float,
    endFraction: Float,
    totalDuration: Float,
    onStartFractionChange: (Float) -> Unit,
    onEndFractionChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val minGap = 1f / totalDuration.coerceAtLeast(1f)
    var containerWidth by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .border(width = 1.dp, color = MyColors.MainColor, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .onGloballyPositioned { containerWidth = it.size.width.toFloat() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MyColors.MainColor.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp))
        )

        DraggableHandle(
            fraction = startFraction,
            containerWidth = containerWidth,
            onFractionChange = { onStartFractionChange(it.coerceAtMost(endFraction - minGap)) },
            isStart = true
        )

        DraggableHandle(
            fraction = endFraction,
            containerWidth = containerWidth,
            onFractionChange = { onEndFractionChange(it.coerceAtLeast(startFraction + minGap)) },
            isStart = false
        )
    }
}

@Composable
fun DraggableHandle(
    fraction: Float,
    containerWidth: Float,
    onFractionChange: (Float) -> Unit,
    isStart: Boolean
) {
    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(4.dp)
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, containerWidth)
                    onFractionChange((offsetX / containerWidth).coerceIn(0f, 1f))
                }
            }
            .background(MyColors.MainColor)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(MyColors.MainColor, CircleShape)
                .align(if (isStart) Alignment.CenterStart else Alignment.CenterEnd)
        )
    }

    LaunchedEffect(fraction, containerWidth) {
        offsetX = fraction * containerWidth
    }
}

//@Composable
//fun AudioCutterScreen(
//    audioPath: String,
//    audioTitle: String,
//    navigateBack: () -> Unit,
//    viewModel: VideoToAudioConverterViewModel = koinViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//    var fileName by remember { mutableStateOf(audioTitle) }
//    var currentTime by remember { mutableStateOf("01:02") }
//    var volume by remember { mutableStateOf(100f) }
//    var isPlaying by remember { mutableStateOf(false) }
//
//    // Audio cutter slider state
//    var startPosition by remember { mutableStateOf(0.2f) } // 20% from start
//    var endPosition by remember { mutableStateOf(0.8f) }   // 80% from start
//    var totalDuration by remember { mutableStateOf(120f) }  // Total duration in seconds
//    val density = LocalDensity.current
//
//    Scaffold(
//        topBar = {
//            AudioCutterTopBar(
//                title = "Audio Cutter",
//                navigateBack = navigateBack
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White)
//                .padding(paddingValues)
//                .padding(horizontal = 18.sdp)
//        ) {
//            // Rename Section
//            Spacer(modifier = Modifier.height(20.sdp))
//            Text(
//                text = "Rename",
//                fontSize = 16.ssp,
//                fontWeight = FontWeight.Medium,
//                color = MyColors.MainColor
//            )
//            Spacer(modifier = Modifier.height(8.sdp))
//
//            BasicTextField(
//                value = fileName,
//                onValueChange = { fileName = it },
//                textStyle = TextStyle(
//                    fontSize = 14.ssp,
//                    color = MyColors.MainColor
//                ),
//                modifier = Modifier.fillMaxWidth()
//            ) { innerTextField ->
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.sdp)
//                ) {
//                    if (fileName.isEmpty()) {
//                        Text(
//                            text = "File Name user can change it",
//                            fontSize = 14.ssp,
//                            color = MyColors.MainColor.copy(alpha = 0.6f)
//                        )
//                    }
//                    innerTextField()
//                }
//            }
//
//            Spacer(modifier = Modifier.height(30.sdp))
//
//            // Audio Timeline Section
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Start Time Control
//                TimeAdjustPill(
//                    timeText = formatTime(startPosition * totalDuration),
//                    onMinus = {
//                        val step = 5f / totalDuration
//                        val minGap = 1f / totalDuration
//                        startPosition = (startPosition - step)
//                            .coerceAtLeast(0f)
//                            .coerceAtMost(endPosition - minGap)
//                    },
//                    onPlus = {
//                        val step = 5f / totalDuration
//                        val minGap = 1f / totalDuration
//                        startPosition = (startPosition + step)
//                            .coerceAtLeast(0f)
//                            .coerceAtMost(endPosition - minGap)
//                    }
//                )
//
//                // Current Time Display
//                Text(
//                    text = formatTime(totalDuration),
//                    fontSize = 18.ssp,
//                    fontWeight = FontWeight.Bold,
//                    color = MyColors.MainColor
//                )
//
//                // End Time Control
//                TimeAdjustPill(
//                    timeText = formatTime(endPosition * totalDuration),
//                    onMinus = {
//                        val step = 5f / totalDuration
//                        val minGap = 1f / totalDuration
//                        endPosition = (endPosition - step)
//                            .coerceAtLeast(startPosition + minGap)
//                            .coerceAtMost(1f)
//                    },
//                    onPlus = {
//                        val step = 5f / totalDuration
//                        val minGap = 1f / totalDuration
//                        endPosition = (endPosition + step)
//                            .coerceAtLeast(startPosition + minGap)
//                            .coerceAtMost(1f)
//                    }
//                )
//            }
//
//            Spacer(modifier = Modifier.height(20.sdp))
//
//            // Volume Control
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                androidx.compose.foundation.Image(
//                    painter = painterResource(R.drawable.ic_volumn), // Using speaker icon
//                    contentDescription = "Volume",
//                    colorFilter = ColorFilter.tint(MyColors.MainColor),
//                    modifier = Modifier.size(26.sdp)
//                )
//
//                Spacer(modifier = Modifier.width(12.sdp))
//
//                Slider(
//                    value = volume,
//                    onValueChange = { volume = it },
//                    valueRange = 0f..100f,
//                    colors = androidx.compose.material3.SliderDefaults.colors(
//                        thumbColor = MyColors.MainColor,
//                        activeTrackColor = MyColors.MainColor,
//                        inactiveTrackColor = MyColors.MainColor.copy(alpha = 0.3f)
//                    ),
//                    modifier = Modifier.weight(1f)
//                )
//
//                Spacer(modifier = Modifier.width(12.sdp))
//
//                Text(
//                    text = "${volume.toInt()}%",
//                    fontSize = 14.ssp,
//                    color = MyColors.MainColor
//                )
//            }
//
//            Spacer(modifier = Modifier.height(20.sdp))
//
//            // Audio Waveform Display with Interactive Cutter Slider
//            AudioCutterSlider(
//                startPosition = startPosition,
//                endPosition = endPosition,
//                totalDuration = totalDuration,
//                onStartPositionChange = { startPosition = it },
//                onEndPositionChange = { endPosition = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(80.sdp)
//            )
//
//            Spacer(modifier = Modifier.height(30.sdp))
//
//            // Playback Controls
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.Bottom
//            ) {
//                // Rewind Button
//
//
//                    Image(
//                        painter = painterResource(R.drawable.ic_reverse_five_sec),
//                        contentDescription = "Rewind",
//                        colorFilter = ColorFilter.tint(MyColors.MainColor),
//                        modifier = Modifier.size(30.sdp)
//                    )
//
//
//
//                Spacer(modifier = Modifier.width(15.sdp))
//
//                // Play/Pause Button
//
//
//                    Image(
//                        painter = painterResource(
//                            if (isPlaying) R.drawable.ic_pause_audio
//                            else R.drawable.ic_play_audio
//                        ),
//                        contentDescription = if (isPlaying) "Pause" else "Play",
//                        modifier = Modifier.size(50.sdp).clickable(){
//                            isPlaying = !isPlaying
//                        }
//                    )
//
//
//
//                Spacer(modifier = Modifier.width(15.sdp))
//
//
//
//                    Image(
//                        painter = painterResource(R.drawable.ic_skip_five_sec),
//                        contentDescription = "Forward",
//                        colorFilter = ColorFilter.tint(MyColors.MainColor),
//                        modifier = Modifier
//                            .size(30.sdp)
//                    )
//
//
//            }
//
//            Spacer(modifier = Modifier.height(40.sdp))
//
//            // Cut Audio Button
//            Button(
//                onClick = { /* Handle cut audio */ },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.sdp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MyColors.MainColor
//                ),
//                shape = RoundedCornerShape(8.sdp)
//            ) {
//                Text(
//                    text = "Cut Audio",
//                    fontSize = 16.ssp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//            }
//
//            Spacer(modifier = Modifier.height(20.sdp))
//        }
//    }
//}
//
//@Composable
//fun AudioCutterTopBar(
//    title: String,
//    navigateBack: () -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 18.sdp, vertical = 15.sdp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        androidx.compose.foundation.Image(
//            painter = painterResource(R.drawable.ic_back_arrow),
//            contentDescription = "Back",
//            colorFilter = ColorFilter.tint(MyColors.MainColor),
//            modifier = Modifier
//                .size(20.sdp)
//                .clickable { navigateBack() }
//        )
//
//        Spacer(modifier = Modifier.width(15.sdp))
//
//        Text(
//            text = title,
//            fontSize = 20.ssp,
//            fontWeight = FontWeight.Bold,
//            color = MyColors.MainColor
//        )
//    }
//}
//
//@Composable
//fun TimeControlButton(
//    text: String,
//    onClick: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .background(
//                color = MyColors.MainColor,
//                shape = RoundedCornerShape(20.sdp)
//            )
//            .padding(horizontal = 12.sdp, vertical = 6.sdp)
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = text,
//            fontSize = 12.ssp,
//            fontWeight = FontWeight.Medium,
//            color = Color.White
//        )
//    }
//}
//
//@Composable
//fun TimeAdjustPill(
//    timeText: String,
//    onMinus: () -> Unit,
//    onPlus: () -> Unit
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Box(
//            modifier = Modifier
//                .background(
//                    color = MyColors.MainColor,
//                    shape = RoundedCornerShape(20.sdp)
//                )
//                .clickable { onMinus() }
//                .padding(horizontal = 8.sdp, vertical = 4.sdp)
//        ) {
//            Text(text = "-", color = Color.White, fontSize = 12.ssp)
//        }
//        Spacer(modifier = Modifier.width(6.sdp))
//        Text(
//            text = timeText,
//            fontSize = 12.ssp,
//            color = MyColors.MainColor,
//            fontWeight = FontWeight.SemiBold
//        )
//        Spacer(modifier = Modifier.width(6.sdp))
//        Box(
//            modifier = Modifier
//                .background(
//                    color = MyColors.MainColor,
//                    shape = RoundedCornerShape(20.sdp)
//                )
//                .clickable { onPlus() }
//                .padding(horizontal = 8.sdp, vertical = 4.sdp)
//        ) {
//            Text(text = "+", color = Color.White, fontSize = 12.ssp)
//        }
//    }
//}
//
//@Composable
//fun AudioCutterSlider(
//    startPosition: Float,
//    endPosition: Float,
//    totalDuration: Float,
//    onStartPositionChange: (Float) -> Unit,
//    onEndPositionChange: (Float) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var containerWidthPx by remember { mutableStateOf(0f) }
//    val minGapFraction = 1f / (totalDuration.coerceAtLeast(1f)) // 1s min gap
//
//    Box(
//        modifier = modifier
//            .border(
//                width = 1.dp,
//                color = MyColors.MainColor,
//                shape = RoundedCornerShape(8.sdp)
//            )
//            .padding(8.sdp)
//            .onGloballyPositioned { layoutCoordinates ->
//                containerWidthPx = layoutCoordinates.size.width.toFloat()
//            }
//    ) {
//        // Background waveform area
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    color = MyColors.MainColor.copy(alpha = 0.1f),
//                    shape = RoundedCornerShape(4.sdp)
//                )
//        ) {
//            // Simulated waveform bars
//            Row(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 4.sdp),
//                horizontalArrangement = Arrangement.SpaceEvenly,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                repeat(50) { index ->
//                    val height = (20 + (index % 7) * 8).dp
//                    Box(
//                        modifier = Modifier
//                            .width(2.dp)
//                            .height(height)
//                            .background(
//                                color = MyColors.MainColor.copy(alpha = 0.6f),
//                                shape = RoundedCornerShape(1.dp)
//                            )
//                    )
//                }
//            }
//
//            // Selection area highlight
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        color = MyColors.MainColor.copy(alpha = 0.2f),
//                        shape = RoundedCornerShape(4.sdp)
//                    )
//            )
//
//            // Start handle
//            DraggableHandle(
//                position = startPosition,
//                onPositionChange = { fraction ->
//                    val clamped = fraction
//                        .coerceAtLeast(0f)
//                        .coerceAtMost(endPosition - minGapFraction)
//                    onStartPositionChange(clamped)
//                },
//                modifier = Modifier.align(Alignment.CenterStart),
//                isStartHandle = true
//            )
//
//            // End handle
//            DraggableHandle(
//                position = endPosition,
//                onPositionChange = { fraction ->
//                    val clamped = fraction
//                        .coerceAtLeast(startPosition + minGapFraction)
//                        .coerceAtMost(1f)
//                    onEndPositionChange(clamped)
//                },
//                modifier = Modifier.align(Alignment.CenterEnd),
//                isStartHandle = false
//            )
//        }
//    }
//}
//
//@Composable
//fun DraggableHandle(
//    position: Float,
//    onPositionChange: (Float) -> Unit,
//    modifier: Modifier = Modifier,
//    isStartHandle: Boolean
//) {
//    var offsetX by remember { mutableStateOf(0f) }
//    val density = LocalDensity.current
//
//    Box(
//        modifier = modifier
//            .width(4.dp)
//            .height(80.sdp)
//            .background(MyColors.MainColor)
//            .offset { IntOffset(offsetX.roundToInt(), 0) }
//            .pointerInput(Unit) {
//                detectDragGestures(
//                    onDragStart = { /* Handle drag start */ },
//                    onDragEnd = { /* Handle drag end */ },
//                    onDrag = { change, dragAmount ->
//                        val containerWidth = size.width.toFloat()
//                        val handleWidth = with(density) { 4.dp.toPx() }
//                        val maxOffset = containerWidth - handleWidth
//                        val startOffset = position * maxOffset
//                        val newOffset = (startOffset + dragAmount.x)
//                            .coerceIn(0f, maxOffset)
//                        val newPosition = (newOffset / maxOffset).coerceIn(0f, 1f)
//                        offsetX = newOffset
//                        onPositionChange(newPosition)
//                    }
//                )
//            }
//    ) {
//        // Handle indicator
//        Box(
//            modifier = Modifier
//                .size(12.dp)
//                .background(
//                    color = MyColors.MainColor,
//                    shape = CircleShape
//                )
//                .align(if (isStartHandle) Alignment.CenterStart else Alignment.CenterEnd)
//        )
//    }
//}
//
//fun formatTime(seconds: Float): String {
//    val minutes = (seconds / 60).toInt()
//    val remainingSeconds = (seconds % 60).toInt()
//    return String.format("%02d:%02d", minutes, remainingSeconds)
//}