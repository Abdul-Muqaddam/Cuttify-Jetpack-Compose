package com.example.videotoaudioconverter.presentation.all_video_files


import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.all_video_files.components.getAllVideos
import com.example.videotoaudioconverter.presentation.all_video_files.components.getVideoDuration
import com.example.videotoaudioconverter.presentation.all_video_files.components.getVideoThumbnail
import com.example.videotoaudioconverter.presentation.home_screen.component.HorizontalSpacer
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AllVideoFiles() {
    val context = LocalContext.current
    var allVideo by remember { mutableStateOf<List<Uri>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val video = getAllVideos(context)
            withContext(Dispatchers.Main) {
                allVideo = video
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(vertical = 10.sdp, horizontal = 16.sdp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${allVideo.size} items", color = MyColors.Green058, fontSize = 18.ssp)
            Image(
                modifier = Modifier.size(22.sdp),
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = null
            )
        }
        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(allVideo) { item ->
                EachVideoComponent(item)
            }
        }
    }
}

fun getFileName(context: Context, uri: Uri): String {
    var name = "Unknown"
    val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        if (cursor.moveToFirst()) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}


@Composable
fun EachVideoComponent(video: Uri) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(video) {
        withContext(Dispatchers.IO) {
            bitmap = getVideoThumbnail(context, video)
        }
    }

    Column {

        bitmap?.let {
            Box() {

                Image(
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.sdp)
                        .size(90.sdp)
                        .clip(RoundedCornerShape(10.sdp))
                )
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(alignment = Alignment.BottomStart)
                        .padding(start = 10.sdp, bottom = 10.sdp)
                ) {
                    Image(
                        modifier = Modifier.size(14.sdp),
                        painter = painterResource(R.drawable.ic_video_sm),
                        contentDescription = null
                    )
                    HorizontalSpacer(2)
                    val durationOfVideo=getVideoDuration(context=context,video)
                    Text(fontSize = 10.ssp, text = durationOfVideo)
                }
            }
        } ?: Image(
            painter = painterResource(R.drawable.ic_video_player),
            contentDescription = null,
            modifier = Modifier
                .padding(4.sdp)
                .size(90.sdp)
        )
        val fileName = getFileName(context = context, uri = video)
        Text(fontSize = 12.ssp, color = MyColors.Green058, text = fileName, maxLines = 1)
    }
}






