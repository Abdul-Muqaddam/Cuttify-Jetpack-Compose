package com.example.videotoaudioconverter.presentation.video_player_screen

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView


@Composable
fun VideoPlayerScreen(videoUri: Uri){
    val context = LocalContext.current
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

    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
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

}