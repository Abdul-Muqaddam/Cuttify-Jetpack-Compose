package com.example.videotoaudioconverter.presentation.SetRingtoneScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun SetRingtoneScreen(
    navigateBackToMainScreen: () -> Unit,
    navigateToAudioSelection: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 15.sdp)) {
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
                    .clickable { navigateBackToMainScreen() }
            )
            Spacer(modifier = Modifier.width(6.sdp))
            Text(
                text = stringResource(R.string.set_ringtone),
                fontSize = 22.ssp,
                color = MyColors.Green058,
                fontWeight = FontWeight.Bold
            )
        }
        SetRingtoneScreenCard(
            mainImg = R.drawable.ic_music_fill,
            mainText = R.string.ringtone,
            rightImg = R.drawable.ic_front_arrow,
            onClick = { navigateToAudioSelection("ringtone") }
        )

        SetRingtoneScreenCard(
            mainImg = R.drawable.ic_bell_fill,
            mainText = R.string.notification,
            rightImg = R.drawable.ic_front_arrow,
            onClick = { navigateToAudioSelection("notification") }
        )

        SetRingtoneScreenCard(
            mainImg = R.drawable.ic_alarm_fill,
            mainText = R.string.alarm,
            rightImg = R.drawable.ic_front_arrow,
            onClick = { navigateToAudioSelection("alarm") }
        )
    }
}

@Composable
fun SetRingtoneScreenCard(mainImg: Int,
                       mainText: Int,
                       rightImg: Int,
                       onClick:()-> Unit={}) {
    Column (modifier = Modifier
        .padding(5.sdp)
        .padding(top = 10.sdp)) {

        Card(
            onClick = onClick,
            colors = CardDefaults.cardColors(Color.White),
            shape = RoundedCornerShape(12.sdp),
            elevation = CardDefaults.cardElevation(2.sdp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.sdp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.sdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(mainImg),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.sdp)
                        .size(26.sdp)
                        .align(Alignment.CenterVertically)
                )

                    Text(
                        text = stringResource(mainText),
                        fontSize = 20.ssp,
                        color = MyColors.Green058
                    )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(rightImg),
                    contentDescription = null,

                )
            }
        }
    }
}
