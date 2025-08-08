package com.example.videotoaudioconverter.presentation.each_video_preview_and_player_screen

import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.home_screen.component.VerticalSpacer
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(UnstableApi::class)
@Composable
fun EachVideoPreviewAndPlayerScreen(
    navigateToSuccessScreen: (String, String) -> Unit,
    navigateToBack: () -> Unit,
    viewModel: EachVideoPreviewAndPlayerScreenViewModel = koinViewModel(),
    videoUri: Uri,
    fileName: String
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        val originalName = fileName.substringBeforeLast('.', fileName)
        viewModel.setVideoFileNameWithoutExtension(originalName)
    }
    val exoPlayer = remember {
        ExoPlayer.Builder(context).setSeekForwardIncrementMs(15000).setSeekBackIncrementMs(15000)
            .build().apply {
                val mediaItem = MediaItem.fromUri(videoUri)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = false
            }
    }

    DisposableEffect(
        Unit
    ) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(modifier = Modifier.background(MyColors.GreenF1C)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()

                .padding(top = 30.sdp, start = 16.sdp, end = 16.sdp, bottom = 20.sdp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(24.sdp)
                        .clickable {
                            navigateToBack()
                        },
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.sdp),
                    color = Color.White,
                    fontSize = 22.ssp,
                    text = stringResource(R.string.video_to_audio)
                )
            }
            Box(
                modifier = Modifier
                    .background(MyColors.MainColor, shape = RoundedCornerShape(50))
                    .padding(horizontal = 15.sdp, vertical = 3.sdp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(22.sdp),
                    painter = painterResource(R.drawable.ic_checkmark_white),
                    contentDescription = null
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 14.sdp)
        ) {

            AndroidView(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                factory = {
                    PlayerView(context).apply {

                        player = exoPlayer
                        useController = true
                        setShowFastForwardButton(true)
                        setShowRewindButton(true)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                })
            Column(modifier = Modifier.weight(1f)) {
                Text(color = Color.White, fontSize = 18.ssp, text = stringResource(R.string.rename))
                VerticalSpacer(6)
                BasicTextField(
                    textStyle = TextStyle(color = Color.White, fontSize = 14.ssp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.sdp),
                    value = state.videoFileNameWithoutExtension,
                    onValueChange = {
                        viewModel.setVideoFileNameWithoutExtension(it)
                    })
                VerticalSpacer(16)
                Button(

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MyColors.Green94F,
                            shape = RoundedCornerShape(20.sdp)
                        ), colors = ButtonDefaults.buttonColors(Color.Transparent), onClick = {
                        extractAudioAndSave(
                            context = context,
                            videoUri = videoUri,
                            fileName = state.videoFileNameWithoutExtension,
                            onComplete = { outputFile ->

                                CoroutineScope(Dispatchers.Main).launch {
                                    navigateToSuccessScreen(
                                        outputFile.nameWithoutExtension,
                                        outputFile.absolutePath
                                    )
                                    Toast.makeText(
                                        context,
                                        "Successfully Completed IN Music Folder",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onFailed = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(
                                        context,
                                        "Something Went Wrong",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            })
                    }) {
                    Text(
                        fontSize = 16.ssp,
                        color = Color.White,
                        text = stringResource(R.string.convert)
                    )
                }
            }
        }

    }
}