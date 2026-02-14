package com.example.videotoaudioconverter.presentation.main_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.home_screen.HomeScreen
import com.example.videotoaudioconverter.presentation.output_screen.OutPutScreen
import com.example.videotoaudioconverter.presentation.shorts_screen.ShortsScreen
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    navigateToAudioPlayerScreen:()->Unit,
    navigateToVideToAudioConverter:(String)->Unit,
    navigateToSettingScreen: () -> Unit,
    navigateToSetRingtoneScreen:()->Unit,
    navigateToAudioCutterSelection:()->Unit,
    navigateToPremiumScreen: ()->Unit,
    navigateToAudioMergeScreen:()->Unit
) {
    val pagerState = rememberPagerState(0, pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val mutableInteraction = remember { MutableInteractionSource() }

    Scaffold(containerColor = Color.White, bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.sdp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val pages = listOf(
                Triple(R.drawable.ic_home, R.string.home, 0),
                Triple(R.drawable.ic_short_video, R.string.shorts, 1),
                Triple(R.drawable.ic_file_manager, R.string.output, 2)
            )

            pages.forEach { (icon, label, index) ->
                val isSelected = pagerState.currentPage == index
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        modifier = Modifier
                            .size(26.sdp)
                            .clickable(
                                indication = null, interactionSource = mutableInteraction
                            ) {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            },
                        colorFilter = ColorFilter.tint(if (isSelected) MyColors.Green058 else Color.Gray),
                        painter = painterResource(icon),
                        contentDescription = null
                    )
                    if (isSelected) {
                        Text(
                            fontSize = 8.ssp,
                            text = stringResource(label),
                            color = MyColors.Green058
                        )
                    }
                }
            }
        }
    }) { paddingValues ->
        HorizontalPager(
            modifier = Modifier.padding(paddingValues), state = pagerState
        ) { page ->
            when (page) {
                0 -> HomeScreen(navigateToVideToAudioConverter={
                    navigateToVideToAudioConverter(it)
                }, navigateToSettingScreen = {
                    navigateToSettingScreen()
                }, navigateToSetRingtoneScreen = {
                    navigateToSetRingtoneScreen()
                },navigateToAudioPlayerScreen={
                    navigateToAudioPlayerScreen()
                }, navigateToAudioCutterSelection = {
                    navigateToAudioCutterSelection()
                }, navigateToPremiumScreen = {
                    navigateToPremiumScreen()
                }, navigateToAudioMergeScreen = {
                    navigateToAudioMergeScreen()
                }
                    )

                1 -> ShortsScreen()
                2 -> OutPutScreen()
            }
        }
    }
}

