package com.example.videotoaudioconverter.presentation.RateUsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun RateUsScreen(
    navigateToSettingScreen: () -> Unit,
    navigateBackToSettingScreen: () -> Unit,
    navigateToPlayStore: () -> Unit,
    navigateToFeedbackScreen: () -> Unit,
    viewModel: RatingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.state.collectAsState().value
    val selectedStars = state.selectedstars
    val feedbackList = listOf(
        "Very Bad" to R.drawable.emoji_angry,
        "Not Great" to R.drawable.emoji_confused,
        "Average" to R.drawable.emoji_neutral,
        "Good" to R.drawable.emoji_smile,
        "Excellent" to R.drawable.emoji_love
    )

    val feedbackText = if (selectedStars in 1..5) feedbackList[selectedStars - 1].first else ""
    val emojiRes = if (selectedStars in 1..5) feedbackList[selectedStars - 1].second else null
    Column(
        modifier = Modifier
            .padding(horizontal = 15.sdp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_baseline_arrow_back),
                contentDescription = null,
                modifier = Modifier
                    .height(22.sdp)
                    .width(22.sdp)
                    .clickable { navigateBackToSettingScreen() }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.rate_us),
                fontSize = 22.ssp,
                color = MyColors.Green058,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.tell_us_about_your_experience_with_the_video_downloader_app),
            color = MyColors.Green058,
            fontSize = 15.ssp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.sdp)
        )

        if (emojiRes != null) {
            Image(
                painter = painterResource(id = emojiRes),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 20.sdp)
                    .height(60.sdp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = feedbackText,
                color = MyColors.Green058,
                fontWeight = FontWeight.Bold,
                fontSize = 16.ssp,
                modifier = Modifier
                    .padding(top = 8.sdp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 20.sdp)
                .align(Alignment.CenterHorizontally)
        ) {
            repeat(5) { index ->
                val starIndex = index + 1
                IconButton(onClick = { viewModel.onStarSelected(starIndex) }) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star $starIndex",
                        tint = if (starIndex <= selectedStars) MyColors.Green058 else Color.Gray,
                        modifier = Modifier
                            .height(30.sdp)
                            .width(30.sdp)
                    )
                }
            }
        }



        Button(
            onClick = {
                if (selectedStars == 5){
                    navigateToPlayStore()
                }
                else{
                    navigateToFeedbackScreen()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.sdp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = MyColors.Green058)
        ) {
            Text(
                text = stringResource(R.string.send_feedback),
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 16.ssp
            )
        }


        Text(
            text = stringResource(R.string.maybe_later),
            fontSize = 15.ssp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable { navigateToSettingScreen() }
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.sdp)
        )
        Spacer(modifier = Modifier.weight(3f))
    }
}