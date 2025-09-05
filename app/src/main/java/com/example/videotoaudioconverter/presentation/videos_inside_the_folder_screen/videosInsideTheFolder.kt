package com.example.videotoaudioconverter.presentation.videos_inside_the_folder_screen

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.all_video_files.components.EachVideoComponent
import com.example.videotoaudioconverter.presentation.all_video_files.components.TopBarFilter
import com.example.videotoaudioconverter.presentation.comman_components.BottomSheet
import com.example.videotoaudioconverter.presentation.video_to_audio_converter.component.TopBar
import com.example.videotoaudioconverter.presentation.videos_inside_the_folder_screen.components.getVideosInFolder
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VideosInsideTheFolderScreen(
    videoClickedForPlayer:(Uri)->Unit,
    fromWhichScreen: String,
    videoClicked: (Uri, String) -> Unit,
    viewModel: VideoInsideTheFolderViewModel = koinViewModel(),
    folderPath: String,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.videoFilesUpdate(
            context = context,
            videos = getVideosInFolder(context, folderPath)
        )
    }
    Scaffold(containerColor = Color.White, topBar = {
        TopBar(
            navigateBack = { navigateBack() },
            searchIconClicked = {
                viewModel.SearchIconClicked()
            },
            crossIconClicked = {
                viewModel.CrossIconClicked()
            },
            onSearchChange = {
                viewModel.onSearchChange(it)
            }, title = stringResource(R.string.select_video)
        )
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TopBarFilter(state.filterVideosList.size, filterClicked = {
                viewModel.isShowBottomSheetChange(true)

            })
            if (state.isShowBottomSheet) {
                BottomSheet(
                    sortFilter = {
                        viewModel.onSortFilterChange(it)
                    },
                    onDismiss = {
                        viewModel.isShowBottomSheetChange(false)
                    },
                )
            }
            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                items(state.filterVideosList) { item ->
                    EachVideoComponent(
                        videoClicked = { videoUri, videoTitle ->
                            if (fromWhichScreen == "from_audio_to_video_converter") {
                                videoClicked(videoUri, videoTitle)
                            } else {
                                videoClickedForPlayer(videoUri)
                            }
                        },
                        video = item.uri,
                        fileName = item.fileName
                    )
                }
            }
        }
    }
}