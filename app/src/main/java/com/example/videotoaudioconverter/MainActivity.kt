package com.example.videotoaudioconverter

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.videotoaudioconverter.navigation.AppNavHost
import com.example.videotoaudioconverter.presentation.NotificationUtil
import com.example.videotoaudioconverter.presentation.Notification_Helper.Notification_Helper
import com.example.videotoaudioconverter.presentation.splashScreen.SplashScreen
import com.example.videotoaudioconverter.ui.theme.VideoToAudioConverterTheme
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private var interstitialAd: InterstitialAd? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        loadInterstitialAd()
        NotificationUtil.createChannel(this)
        Notification_Helper.createAudioChannel(this)
        enableEdgeToEdge()
        setContent {
            VideoToAudioConverterTheme {

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted -> }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }


                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {

                    // Splash Screen
                    composable("splash") {
                        SplashScreen(
                            navigateToHome = {
                                navController.navigate("ad_screen") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }


                    composable("ad_screen") {
                        LaunchedEffect(Unit) {
                            showInterstitialAd {
                                navController.navigate("main") {
                                    popUpTo("ad_screen") { inclusive = true }
                                }
                            }
                        }
                    }

                    // Main Screen
                    composable("main") {
                        AppNavHost()
                    }

                }

            }
        }
//        setContent {
//            VideoToAudioConverterTheme {
//
//                val launcher = rememberLauncherForActivityResult(
//                    contract = ActivityResultContracts.RequestPermission()
//                ) { isGranted ->
//
//                }
//
//                LaunchedEffect(Unit) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                    }
//                }
//
//                AppNavHost()
//            }
//        }
    }

    private fun loadInterstitialAd() {

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712", // Test Ad Unit
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    private fun showInterstitialAd(onAdDismissed: () -> Unit) {

        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        onAdDismissed()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        interstitialAd = null
                        onAdDismissed()
                    }
                }

            interstitialAd?.show(this)
        } else {
            onAdDismissed()
        }
    }
}


