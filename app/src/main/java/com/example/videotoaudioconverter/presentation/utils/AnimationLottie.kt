package com.example.videotoaudioconverter.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun AnimationLottie(modifier: Modifier= Modifier, animation:Int,isOneTimeAnimation: Boolean=true){
        val preloaderLottieComposition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(
                animation
            )
        )

        val preloaderProgress by animateLottieCompositionAsState(
            preloaderLottieComposition,
            iterations = if(isOneTimeAnimation) 1 else LottieConstants.IterateForever,
            isPlaying = true
        )


        LottieAnimation(
            composition = preloaderLottieComposition,
            progress = {
                preloaderProgress
            } ,
            modifier = modifier
        )
}