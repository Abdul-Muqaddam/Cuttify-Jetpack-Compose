package com.example.videotoaudioconverter.presentation.all_video_files.components

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.videotoaudioconverter.presentation.home_screen.component.HorizontalSpacer
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EachVideoComponent(videoClicked:(Uri, String)->Unit, video: Uri, fileName: String) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(video) {
        withContext(Dispatchers.IO) {
            bitmap = getVideoThumbnail(context, video)
        }
    }

    val interactionSource=remember { MutableInteractionSource() }
    Column (modifier = Modifier.clickable(indication = null, interactionSource = interactionSource){
        videoClicked(video,fileName)
    }){

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
//        val fileName = getFileName(context = context, uri = video)
        Text(fontSize = 12.ssp, color = MyColors.Green058, text = fileName, maxLines = 1)
    }
}