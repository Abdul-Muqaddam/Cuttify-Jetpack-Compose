package com.example.videotoaudioconverter.presentation.adsMob

import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.videotoaudioconverter.R
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAdView
import ir.kaaveh.sdpcompose.sdp

@Composable
fun NativeAdComposable() {

    AndroidView(
        modifier = Modifier.fillMaxWidth().height(130.sdp),
        factory = { context ->

            // Container that will hold the ad
            val container = FrameLayout(context)

            val adLoader = AdLoader.Builder(
                context,
                "ca-app-pub-3940256099942544/2247696110" // Test Native Ad ID
            )
                .forNativeAd { nativeAd ->

                    val adView = LayoutInflater.from(context)
                        .inflate(R.layout.native_ad_layout, container, false) as NativeAdView

                    val headline = adView.findViewById<TextView>(R.id.ad_headline)
                    headline.text = nativeAd.headline

                    adView.headlineView = headline
                    adView.setNativeAd(nativeAd)

                    container.removeAllViews()
                    container.addView(adView)
                }
                .build()

            val adRequest = AdRequest.Builder().build()
            adLoader.loadAd(adRequest)

            container
        }
    )
}
