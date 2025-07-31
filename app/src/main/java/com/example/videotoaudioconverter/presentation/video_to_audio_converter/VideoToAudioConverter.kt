package com.example.videotoaudioconverter.presentation.video_to_audio_converter

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.all_folder.AllFolder
import com.example.videotoaudioconverter.presentation.home_screen.component.VerticalSpacer
import com.example.videotoaudioconverter.presentation.all_video_files.AllVideoFiles
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.launch

@Composable
fun VideoToAudioConverterScreen(navigateBack: () -> Unit) {

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()



    Scaffold(topBar = {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 3.sdp, start = 18.sdp, end = 18.sdp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(20.sdp)
                        .clickable() {
                            navigateBack()
                        },
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.sdp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.ssp,
                    text = stringResource(R.string.select_video),
                    color = MyColors.MainColor
                )
            }
            Image(
                modifier = Modifier.size(24.sdp),
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null
            )
        }
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
                    0 -> AllVideoFiles()
                    1 -> AllFolder()

                }
            }
        }
    }
}

