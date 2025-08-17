package com.example.videotoaudioconverter.presentation.success_screen

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.home_screen.component.FeatureCard
import com.example.videotoaudioconverter.presentation.home_screen.component.VerticalSpacer
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun SuccessScreen(fileName: String, filePath: String) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var lastPosition by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
    LaunchedEffect(Unit) {
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
            lastPosition = 0
            mediaPlayer.reset()
        }
    }

    Column(
        modifier = Modifier
            .padding(15.sdp)
            .statusBarsPadding()
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Image(
                modifier = Modifier.size(36.sdp),
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
                    width = 2.sdp,
                    color = MyColors.MainColor,
                    shape = RoundedCornerShape(10.sdp)
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
            VerticalSpacer(5)
            Image(
                modifier = Modifier
                    .size(40.sdp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        if (isPlaying) {
                            isPlaying=false
                            lastPosition = mediaPlayer.currentPosition
                            mediaPlayer.pause()
                        } else {
                            isPlaying=true
                            try {
                                if (lastPosition > 0) {
                                    mediaPlayer.seekTo(lastPosition)
                                    mediaPlayer.start()
                                } else {
                                    mediaPlayer.reset()
                                    mediaPlayer.setDataSource(filePath)
                                    mediaPlayer.prepare()
                                    mediaPlayer.start()
                                }
                            } catch (e: Exception) {
                                Log.d("success screen playing error", e.toString())
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }, painter =
                    if (isPlaying) {
                        painterResource(R.drawable.ic_pause_success)
                    } else {
                        painterResource(R.drawable.ic_play_success)
                    }, contentDescription = null
            )
        }
        VerticalSpacer(30)
        Text(
            text = stringResource(R.string.you_may_also_need),
            color = MyColors.MainColor,
            fontSize = 16.ssp
        )
        VerticalSpacer(10)
        Column {

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
                    imgWidth = 65,
                    imgHeight = 30,
                    img = R.drawable.ic_ringtoon,
                    text = stringResource(R.string.set_ringtone),
                    onClick = {}
                )
                FeatureCard(
                    imgWidth = 30,
                    imgHeight = 30,
                    img = R.drawable.ic_audio__player,
                    text = stringResource(R.string.audio_player),
                    onClick = {}
                )

            }
            VerticalSpacer(10)
            FeatureCard(
                imgWidth = 50,
                imgHeight = 30,
                img = R.drawable.ic_audio_cutter,
                text = stringResource(R.string.audio_cutter),
                onClick = {}
            )
        }
    }
}

