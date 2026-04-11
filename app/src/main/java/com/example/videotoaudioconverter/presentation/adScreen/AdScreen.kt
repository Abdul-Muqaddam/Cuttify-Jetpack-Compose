package com.example.videotoaudioconverter.presentation.adScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.ads.interstitial.InterstitialAd

@Composable
fun AdScreen(
    interstitialAd: InterstitialAd?,
    onAdDismissed: () -> Unit
) {
    // Blank screen shown while ad appears
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    )

    LaunchedEffect(Unit) {
        onAdDismissed() // This will be triggered from Activity, not here
    }
}