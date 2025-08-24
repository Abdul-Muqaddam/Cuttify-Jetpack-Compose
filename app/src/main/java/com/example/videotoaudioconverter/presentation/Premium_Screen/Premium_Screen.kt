package com.example.videotoaudioconverter.presentation.Premium_Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import org.w3c.dom.Text

@Composable
fun Premium_Screen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        Column(
            modifier = Modifier
                .padding(horizontal = 18.sdp)
                .padding(top = 40.sdp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.go_premium),
                    color = MyColors.Green058,
                    fontSize = 22.ssp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))

            }
            Image(
                painter = painterResource(R.drawable.premium_trophy),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 10.sdp)
                    .align(alignment = Alignment.CenterHorizontally),
            )
            Text(
                text = stringResource(R.string._10_10),
                color = MyColors.Green058,
                fontSize = 25.ssp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(top = 10.sdp)
            )
            Text(
                text = stringResource(R.string.superior_audio_editing_experience),
                fontSize = 15.ssp,
                color = MyColors.Green226,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            premiumtitle(
                img = R.drawable.ic_tick,
                text = R.string.even_higher_quality
            )
            premiumtitle(
                img = R.drawable.ic_tick,
                text = R.string.remove_ads
            )
            premiumtitle(
                img = R.drawable.ic_tick,
                text = R.string.completely_unlimited_access
            )
            premiumtitle(
                img = R.drawable.ic_tick,
                text = R.string.instant_support
            )
            premiumtitle(
                img = R.drawable.ic_tick,
                text = R.string.and_much_more
            )
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.sdp)
                    .height(40.sdp)
                    .padding(horizontal = 10.sdp),
                shape = RoundedCornerShape(12.sdp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyColors.Green058,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.sdp,
                    pressedElevation = 6.sdp
                )
            ) {
                Text(
                    text = stringResource(R.string.subscribe_12_year),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    fontSize = 15.ssp
                )
            }
            Text(
                text = "Auto-renewal and cancel anytime",
                color = MyColors.greyD56_80,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(top = 3.sdp)
            )
        }
    }
}

@Composable
fun premiumtitle(
    img: Int,
    text: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 7.sdp)
    ) {
        Image(
            painter = painterResource(img),
            contentDescription = null
        )
        Spacer(modifier = Modifier.padding(5.sdp))
        Text(
            text = stringResource(text),
            fontSize = 12.ssp,
            color = MyColors.Green226
        )

    }

}