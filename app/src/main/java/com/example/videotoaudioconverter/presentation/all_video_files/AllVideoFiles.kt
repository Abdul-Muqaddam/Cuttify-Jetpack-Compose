package com.example.videotoaudioconverter.presentation.all_video_files


import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.videotoaudioconverter.presentation.all_folder.VideoToAudioConverterViewModelState
import com.example.videotoaudioconverter.presentation.all_video_files.components.EachVideoComponent
import com.example.videotoaudioconverter.presentation.all_video_files.components.TopBarFilter
import com.example.videotoaudioconverter.presentation.all_video_files.components.getAllVideos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AllVideoFiles(state: VideoToAudioConverterViewModelState, listOfAllVideos:(List<Uri>)-> Unit) {
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val video = getAllVideos(context)
            withContext(Dispatchers.Main) {
                listOfAllVideos(video)
//                allVideo = video
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarFilter(state.filteredVideos.size)
        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(state.filteredVideos) { item ->
                EachVideoComponent(item)
            }
        }
    }
}