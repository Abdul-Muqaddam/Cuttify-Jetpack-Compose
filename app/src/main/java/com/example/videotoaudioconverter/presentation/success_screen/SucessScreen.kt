    package com.example.videotoaudioconverter.presentation.success_screen

    import android.content.Intent
    import android.media.MediaPlayer
    import android.net.Uri
    import android.util.Log
    import android.widget.Toast
    import androidx.compose.animation.core.Animatable
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.interaction.MutableInteractionSource
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.statusBarsPadding
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.LinearProgressIndicator
    import androidx.compose.material3.Slider
    import androidx.compose.material3.SliderDefaults
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.DisposableEffect
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableFloatStateOf
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.runtime.saveable.rememberSaveable
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.core.content.FileProvider
    import com.example.lifelinepro.presentation.comman.formatDurationVideo
    import com.example.videotoaudioconverter.R
    import com.example.videotoaudioconverter.presentation.home_screen.component.FeatureCard
    import com.example.videotoaudioconverter.presentation.home_screen.component.HorizontalSpacer
    import com.example.videotoaudioconverter.presentation.home_screen.component.VerticalSpacer
    import com.example.videotoaudioconverter.ui.theme.MyColors
    import ir.kaaveh.sdpcompose.sdp
    import ir.kaaveh.sdpcompose.ssp
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import org.koin.androidx.compose.koinViewModel
    import java.io.File

    @Composable
    fun SuccessScreen(
        fileName: String,
        filePath: String,
        viewModel: SuccessScreenViewModel = koinViewModel()
    ) {
        val state by viewModel.state.collectAsState()
        val context = LocalContext.current
        val mediaPlayer = remember { MediaPlayer() }
        val progress = remember { Animatable(0f) }
        var sliderPosition by remember { mutableFloatStateOf(0f) }

        val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", File(filePath))


        DisposableEffect(Unit) {
            onDispose {
                mediaPlayer.release()
            }
        }
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            mediaPlayer.setOnCompletionListener {
                scope.launch {
                    viewModel.isPlayingUpdate(false)
                     viewModel.lastPositionUpdate(0)
                    progress.snapTo(0f)
                    mediaPlayer.reset()
                }
            }
        }

        LaunchedEffect(filePath) {
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(filePath)
                mediaPlayer.prepare()
                viewModel.formatedDurationUpdate(formatDurationVideo(mediaPlayer.duration))
                mediaPlayer.reset()
            } catch (e: Exception) {
                Log.e("SuccessScreen", "Error loading audio duration", e)
                Toast.makeText(context, "Unable to load audio", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit) {
            while (true) {
                if (mediaPlayer.isPlaying || mediaPlayer.currentPosition > 0) {
                    val duration = mediaPlayer.duration.takeIf { it > 0 } ?: 1
                    sliderPosition = mediaPlayer.currentPosition.toFloat() / duration.toFloat()
                }
                delay(200)
            }
        }

        LaunchedEffect(state.isPlaying) {
            if (state.isPlaying) {
                while (state.isPlaying && mediaPlayer.isPlaying) {
                    val ratio = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
                    progress.snapTo(ratio)
                    viewModel.formatedCurrentPositionUpdate(formatDurationVideo(mediaPlayer.currentPosition))
                    kotlinx.coroutines.delay(100L)
                }
            } else {
                if (state.lastPosition == 0) {
                    viewModel.formatedCurrentPositionUpdate("00:00")
                    progress.snapTo(0f)
                }
            }
        }


        Column(
            modifier = Modifier
                .padding(15.sdp)
                .statusBarsPadding()
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Image(
                    modifier = Modifier.size(36.sdp).clickable(){
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, fileUri)
                            type = context.contentResolver.getType(fileUri) ?: "*/*"
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share via")
                        context.startActivity(shareIntent)
                    },
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = null
                )
            }
            VerticalSpacer(20)
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.sdp, color = MyColors.MainColor, shape = RoundedCornerShape(10.sdp)
                    )
                    .padding(vertical = 20.sdp)
            ) {
                Image(
                    modifier = Modifier.size(45.sdp),
                    painter = painterResource(R.drawable.ic_audio),
                    contentDescription = null
                )
                VerticalSpacer(10)
                Text(
                    fontWeight = FontWeight.SemiBold,
                    color = MyColors.MainColor,
                    text = stringResource(R.string.completed_successfully),
                    fontSize = 18.ssp
                )
                VerticalSpacer(5)
                Text(
                    color = MyColors.MainColor,
                    text = fileName
                )
                VerticalSpacer(10)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = state.formatedCurrentPosition, color = MyColors.MainColor)
                    HorizontalSpacer(6)
                    Slider(
                        value = sliderPosition,
                        onValueChange = { newValue ->
                            sliderPosition = newValue
                        },
                        onValueChangeFinished = {
                            val seekPosition = (sliderPosition * mediaPlayer.duration).toInt()
                            mediaPlayer.seekTo(seekPosition)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = MyColors.MainColor,
                            activeTrackColor = MyColors.MainColor,
                            inactiveTrackColor = Color.Gray
                        ),
                        modifier = Modifier.width(150.sdp)
                    )
                    HorizontalSpacer(6)
                    Text(
                        text = state.formatedDuration,
                        fontSize = 14.ssp,
                        color = MyColors.MainColor
                    )
                }
                VerticalSpacer(15)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(24.sdp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {

                                val newPosition = mediaPlayer.currentPosition - 5000
                                if (newPosition > 0) {
                                    mediaPlayer.seekTo(newPosition)
                                } else {
                                    mediaPlayer.seekTo(0)
                                }

                            },
                        painter = painterResource(R.drawable.ic_reverse_five_sec),
                        contentDescription = null
                    )
                    HorizontalSpacer(15)
                    Image(
                        modifier = Modifier
                            .size(40.sdp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {
                                if (state.isPlaying) {
                                    viewModel.isPlayingUpdate(false)
                                    viewModel.lastPositionUpdate(mediaPlayer.currentPosition)
                                    mediaPlayer.pause()
                                } else {
                                    viewModel.isPlayingUpdate(true)
                                    try {
                                        if (state.lastPosition > 0) {
                                            mediaPlayer.seekTo(state.lastPosition)
                                            mediaPlayer.start()
                                        } else {
                                            mediaPlayer.reset()
                                            mediaPlayer.setDataSource(filePath)
                                            mediaPlayer.prepare()
                                            mediaPlayer.start()
                                        }
                                    } catch (e: Exception) {
                                        Log.d("success screen playing error", e.toString())
                                        Toast.makeText(
                                            context,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                            }, painter =
                            if (state.isPlaying) {
                                painterResource(R.drawable.ic_pause_success)
                            } else {
                                painterResource(R.drawable.ic_play_success)
                            }, contentDescription = null
                    )
                    HorizontalSpacer(15)
                    Image(
                        modifier = Modifier
                            .size(24.sdp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {

                                val newPosition = mediaPlayer.currentPosition + 5000
                                if (newPosition < mediaPlayer.duration) {
                                    mediaPlayer.seekTo(newPosition)
                                } else {
                                    mediaPlayer.seekTo(mediaPlayer.duration)
                                }

                            },
                        painter = painterResource(R.drawable.ic_skip_five_sec),
                        contentDescription = null
                    )
                }
            }
            VerticalSpacer(30)
            Text(
                text = stringResource(R.string.you_may_also_need),
                color = MyColors.MainColor,
                fontSize = 16.ssp
            )
            VerticalSpacer(10)


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                FeatureCard(
                    imgWidth = 65,
                    imgHeight = 30,
                    img = R.drawable.ic_audio_merge,
                    text = stringResource(R.string.audio_merge),
                    onClick = {}
                )
                FeatureCard(
                    imgWidth = 50,
                    imgHeight = 30,
                    img = R.drawable.ic_audio_cutter,
                    text = stringResource(R.string.audio_cutter),
                    onClick = {}
                )
                FeatureCard(
                    imgWidth = 65,
                    imgHeight = 30,
                    img = R.drawable.ic_ringtoon,
                    text = stringResource(R.string.set_ringtone),
                    onClick = {}
                )
            }
        }
    }