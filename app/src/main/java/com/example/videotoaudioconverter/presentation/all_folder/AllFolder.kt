package com.example.videotoaudioconverter.presentation.all_folder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.videotoaudioconverter.presentation.all_folder.components.FolderComponentUi
import com.example.videotoaudioconverter.presentation.all_video_files.components.TopBarFilter

@Composable
fun AllFolder(
    listOfAllFolders: (List<VideoFolder>) -> Unit,
    navigateToFolderVideos: (String) -> Unit,
    state: VideoToAudioConverterViewModelState,
    sortFilter: (String) -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        listOfAllFolders(getAllVideoFolders(context))
    }


    Column(modifier = Modifier.fillMaxSize()) {
        TopBarFilter(
            totalItems = state.filteredFolders.size,
            filterClicked = {
                    selectedOption ->
                sortFilter(selectedOption)
            }
        )
        LazyColumn {
            items(state.filteredFolders) { folderModel ->
                FolderComponentUi(
                    folderPath = folderModel.path,
                    folderName = folderModel.name,
                    navigateToFolderVideos = {
                        navigateToFolderVideos(it)
                    })
            }
        }

    }
}


