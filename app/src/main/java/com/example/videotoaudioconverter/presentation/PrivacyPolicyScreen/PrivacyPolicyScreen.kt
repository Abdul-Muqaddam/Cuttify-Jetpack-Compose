package com.example.videotoaudioconverter.presentation.privacy_Policy_Screen

import android.annotation.SuppressLint
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.videotoaudioconverter.R
import ir.kaaveh.sdpcompose.sdp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PrivacyPolicyScreen(
    navController: NavController,
    url: String = "https://www.termsfeed.com/live/6d8c8e15-00af-4a5a-bebf-a2654ae6b686"
){
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var webview: WebView? by remember { mutableStateOf(null) }
    var canGoBack by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privay Policy") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (canGoBack){
                            webview?.goBack()
                        }else{
                            navController.popBackStack()
                        }
                    }) {
                        Icon(painter = painterResource(R.drawable.ic_back_arrow),
                            contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        webview?.reload()
                        isLoading = true
                        hasError = false
                    }) {
                        Icon(painter = painterResource(R.drawable.ic_refresh)
                            , contentDescription = null,
                            modifier = Modifier.size(20.sdp))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            if (isLoading && !hasError){
                Column(modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.sdp))
                    Text("Loading Privacy Policy .....")
                }
            }
            if (hasError){
                Column(modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Failed to load privacy policy",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.sdp))
                    Button(onClick = {
                        webview?.reload()
                        isLoading = true
                        hasError = false
                    }) {
                        Text("Retry")
                    }
                }
            }
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false

                        webViewClient=object :  WebViewClient(){
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon:  android.graphics.Bitmap?
                            ) {
                                super.onPageStarted(view, url, favicon)
                                isLoading  = true
                                canGoBack = view?.canGoBack()?:false
                            }

                            override fun onPageFinished(
                                view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading =false
                                canGoBack = view?.canGoBack() ?: false
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                isLoading = false
                                hasError = true
                            }
                        }
                        loadUrl(url)
                    }
                },
                update = { view->
                    webview =view
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}