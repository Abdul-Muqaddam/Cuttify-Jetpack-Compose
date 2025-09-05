package com.example.videotoaudioconverter.presentation.video_to_audio_converter

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.all_folder.AllFolder
import com.example.videotoaudioconverter.presentation.all_folder.VideoToAudioConverterViewModel
import com.example.videotoaudioconverter.presentation.all_video_files.AllVideoFiles
import com.example.videotoaudioconverter.presentation.home_screen.component.VerticalSpacer
import com.example.videotoaudioconverter.presentation.video_to_audio_converter.component.TopBar
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VideoToAudioConverterScreen(
    videoClickedForPlayer:(Uri)->Unit,
    fromWhichScreen: String,
    navigateToFolderVideos: (String) -> Unit,
    videoClicked: (Uri, String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: VideoToAudioConverterViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopBar(state = state, navigateBack = navigateBack, searchIconClicked = {
            viewModel.SearchIconClicked()
        }, crossIconClicked = {
            viewModel.CrossIconClicked()
        }, onSearchChange = {
            if (pagerState.currentPage == 0) {
                viewModel.onSearchChangeForVideos(value = it)
            } else {
                viewModel.onSearchChangeForFolder(value = it)
            }
        }, title = stringResource(R.string.select_video))
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            VerticalSpacer(26)

            val tabs = listOf(
                Pair(R.string.all, 0),
                Pair(R.string.folder, 1)
            )
            Row {
                tabs.forEach { (title, index) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable() {
                                scope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            }
                    ) {
                        Text(
                            color =
                                if (pagerState.currentPage == index) {
                                    MyColors.Green058
                                } else {
                                    Color.Black

                                },
                            fontSize = 16.ssp,
                            text = stringResource(title)
                        )
                        VerticalSpacer(5)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.sdp)
                                .background(
                                    color =
                                        if (pagerState.currentPage == index) {
                                            MyColors.Green058
                                        } else {
                                            MyColors.grayD9
                                        }
                                ),
                        )
                    }
                }
            }
            HorizontalPager(
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {

                    },
                state = pagerState
            ) { page ->
                when (page) {
                    0 -> AllVideoFiles(videoClicked = { videoUri, videoTitle ->

                        if(fromWhichScreen=="from_audio_to_video_converter"){

                        videoClicked(videoUri, videoTitle)
                        }else{
                            videoClickedForPlayer(videoUri)

                        }
                    }, state = state, listOfAllVideos = {
                        viewModel.saveAllVideos(videos = it, context = context)
                    }, sortFilter = {
                        viewModel.onSortFilterChange(context, it)
                    })

                    1 -> AllFolder(navigateToFolderVideos = {
                        navigateToFolderVideos(it)
                    }, state = state, listOfAllFolders = {
                        viewModel.folderListUpdate(it)
                    })

                }
            }
        }
    }
}