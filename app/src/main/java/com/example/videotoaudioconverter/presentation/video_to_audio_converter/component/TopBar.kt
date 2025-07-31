package com.example.videotoaudioconverter.presentation.video_to_audio_converter.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.all_folder.VideoToAudioConverterViewModelState
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun TopBar(
    onSearchChange: (String) -> Unit,
    state: VideoToAudioConverterViewModelState,
    navigateBack: () -> Unit,
    searchIconClicked: () -> Unit,
    crossIconClicked: () -> Unit
) {



    Row(
        modifier = Modifier
            .height(55.sdp)
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
            if (state.IdealTopBar) {
                Text(
                    modifier = Modifier.padding(start = 4.sdp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.ssp,
                    text = stringResource(R.string.select_video),
                    color = MyColors.MainColor
                )
            } else {

                Box() {
                    BasicTextField(
                        textStyle = TextStyle(
                            fontSize = 16.ssp
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .padding(start = 10.sdp),
//                            .focusRequester(focusRequester)
//                            .focusable(),
                        value = state.searchText,
                        onValueChange = {
                            onSearchChange(it)
                        })

                    if (state.searchText.isEmpty()) {
                        Text(
                            fontSize = 16.ssp,
                            modifier = Modifier.padding(start = 10.sdp, bottom = 3.sdp),
                            text = stringResource(
                                R.string.search
                            ),
                            color = Color.Black
                        )
                    }
                }
            }
        }
        Image(
            modifier = Modifier
                .size(20.sdp)
                .clickable {
                    if (state.IdealTopBar) {
                        searchIconClicked()
                    } else {
                        crossIconClicked()
                    }
                }, painter = if (state.IdealTopBar) {
                painterResource(R.drawable.ic_search)
            } else {
                painterResource(R.drawable.ic_cross)
            }, contentDescription = null
        )
    }
}