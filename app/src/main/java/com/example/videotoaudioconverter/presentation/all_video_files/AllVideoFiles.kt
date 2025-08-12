package com.example.videotoaudioconverter.presentation.all_video_files


import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.videotoaudioconverter.presentation.all_folder.VideoToAudioConverterViewModelState
import com.example.videotoaudioconverter.presentation.all_video_files.components.EachVideoComponent
import com.example.videotoaudioconverter.presentation.all_video_files.components.TopBarFilter
import com.example.videotoaudioconverter.presentation.all_video_files.components.getAllVideos
import com.example.videotoaudioconverter.presentation.comman_components.BottomSheet
import com.example.videotoaudioconverter.ui.theme.MyColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllVideoFiles(
    sortFilter: (String) -> Unit,
    videoClicked: (Uri, String) -> Unit,
    state: VideoToAudioConverterViewModelState,
    listOfAllVideos: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    var bottomSheetState by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val video = getAllVideos(context)
            withContext(Dispatchers.Main) {
                listOfAllVideos(video)
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarFilter(totalItems = state.filteredVideos.size, filterClicked = {
            bottomSheetState = !bottomSheetState
        })
        if (state.filteredVideos.isNotEmpty()) {

            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                items(state.filteredVideos) { item ->
                    EachVideoComponent(videoClicked = { videoUri, videoTitle ->
                        videoClicked(videoUri, videoTitle)
                    }, video = item.uri, fileName = item.fileName)
                }
            }
            if (bottomSheetState) {
                BottomSheet(onDismiss = {
                    bottomSheetState = !bottomSheetState
                }, sortFilter = { sortFilterString ->
                    sortFilter(sortFilterString)
                })

            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(color = MyColors.MainColor)
            }
        }
    }
}