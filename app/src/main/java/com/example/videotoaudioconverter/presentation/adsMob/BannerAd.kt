package com.example.videotoaudioconverter.presentation.adsMob

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd() {

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->

            val adView = AdView(context)
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"

            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)

            adView
        }
    )
}
