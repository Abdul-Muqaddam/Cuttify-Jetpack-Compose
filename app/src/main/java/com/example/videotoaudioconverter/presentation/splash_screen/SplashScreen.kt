package com.example.videotoaudioconverter.presentation.splash_screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.utils.AnimationLottie
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun SplashScreen() {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000)
        )
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(vertical = 40.sdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            color = MyColors.MainColor,
            text = stringResource(R.string.app_name),
            fontSize = 28.ssp
        )
        AnimationLottie(
            isOneTimeAnimation = false,
            animation = R.raw.cutting,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.sdp),
            progress = { progress.value },
            color = MyColors.MainColor,
            trackColor = Color.Gray
        )
        Text(text = "${(progress.value * 100).toInt()}%")
    }
}
