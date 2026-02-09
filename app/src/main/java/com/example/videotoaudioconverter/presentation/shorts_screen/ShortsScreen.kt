package com.example.videotoaudioconverter.presentation.shorts_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

private const val API_KEY = "AIzaSyB5i4GGtGYyzn0GDH6z8JaphX4AxbCVt9o"
private val EXCLUDE_KEYWORDS = listOf("5 min craft", "songs", "sports", "gaming")


@Composable
fun ShortsScreen() {
    var shortsVideos by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            shortsVideos = fetchRandomShorts(API_KEY)
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        errorMessage != null -> Text("Error: $errorMessage", modifier = Modifier.padding(16.sdp))
        shortsVideos.isEmpty() -> Text("No Shorts Found", modifier = Modifier.padding(16.sdp))
        else -> {
            val pagerState = rememberPagerState()
            VerticalPager(
                count = shortsVideos.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                ShortVideoItem(videoId = shortsVideos[page])
            }
        }
    }
}

@Composable
fun ShortVideoItem(videoId: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                webViewClient = WebViewClient()
                loadUrl("https://www.youtube.com/shorts/$videoId?autoplay=1")
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

// Fetch random Shorts globally with filtering
suspend fun fetchRandomShorts(apiKey: String): List<String> {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val shortsVideos = mutableListOf<String>()

        // 1️⃣ Fetch random videos using Search API
        try {
            val searchUrl =
                "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=shorts&maxResults=50&key=$apiKey"
            val searchResponse = client.newCall(Request.Builder().url(searchUrl).build()).execute()
            val searchBody = searchResponse.body?.string() ?: ""
            val searchJson = JSONObject(searchBody)

            if (searchJson.has("items")) {
                val items = searchJson.getJSONArray("items")
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val videoId = item.getJSONObject("id").getString("videoId")
                    val title = item.getJSONObject("snippet").getString("title")

                    // 2️⃣ Filter out unwanted keywords
                    val containsExcluded = EXCLUDE_KEYWORDS.any { title.contains(it, ignoreCase = true) }
                    if (!containsExcluded) {
                        shortsVideos.add(videoId)
                    }
                }
            }
        } catch (_: Exception) {}

        // 3️⃣ Fetch contentDetails to filter videos <= 60 seconds
        val finalShorts = mutableListOf<String>()
        val batchSize = 50
        for (i in shortsVideos.indices step batchSize) {
            val batch = shortsVideos.subList(i, minOf(i + batchSize, shortsVideos.size))
            val idsParam = batch.joinToString(",")
            try {
                val videosUrl =
                    "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id=$idsParam&key=$apiKey"
                val videosResponse = client.newCall(Request.Builder().url(videosUrl).build()).execute()
                val videosBody = videosResponse.body?.string() ?: continue
                val videosJson = JSONObject(videosBody)

                if (!videosJson.has("items")) continue
                val videoItems = videosJson.getJSONArray("items")
                for (j in 0 until videoItems.length()) {
                    val video = videoItems.getJSONObject(j)
                    val duration = video.getJSONObject("contentDetails").getString("duration")
                    if (isoDurationToSeconds(duration) <= 60) {
                        finalShorts.add(video.getString("id"))
                    }
                }
            } catch (_: Exception) {}
        }

        // 4️⃣ Shuffle for randomness like YouTube Shorts
        finalShorts.shuffle()

        finalShorts
    }
}

// Convert ISO 8601 duration to seconds
fun isoDurationToSeconds(duration: String): Int {
    val regex = Regex("PT(?:(\\d+)M)?(?:(\\d+)S)?")
    val match = regex.find(duration)
    val minutes = match?.groups?.get(1)?.value?.toIntOrNull() ?: 0
    val seconds = match?.groups?.get(2)?.value?.toIntOrNull() ?: 0
    return minutes * 60 + seconds
}
