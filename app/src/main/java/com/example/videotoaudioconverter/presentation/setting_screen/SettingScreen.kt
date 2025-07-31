package com.example.videotoaudioconverter.presentation.setting_screen

import androidx.compose.ui.graphics.Color
import android.provider.CalendarContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun SettingScreen(navigateToLanguageScreen:()-> Unit) {
    Column{
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(160.sdp)
            .padding(top = 35.sdp),
            contentAlignment = Alignment.Center
            ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(R.drawable.ic_main_gradient),
                contentDescription = null
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.ic_crown),contentDescription = null,
                        modifier = Modifier
                            .width(65.sdp)
                            .height(65.sdp))
                    Column(modifier = Modifier.padding(start = 14.sdp)) {
                        Text(text = stringResource(R.string.upgrade_premium),
                            color = Color.White,
                            fontSize = 22.ssp,
                            fontWeight = FontWeight.Bold
                            )
                        Text(text = stringResource(R.string.enjoy_all_premium_features),
                            color = Color.White,
                            fontSize = 13.ssp,
                            modifier = Modifier.padding(top = 10.sdp, bottom = 3.sdp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 15.sdp)
        ) {


            Text(
                text = stringResource(R.string.general),
                fontSize = 20.ssp,
                fontWeight = FontWeight.Bold,
                color = MyColors.Green058,
                modifier = Modifier.padding(top = 5.sdp)
            )
            SettingsScreenCard(
                onClick = {navigateToLanguageScreen()},
                mainImg = R.drawable.ic_globe,
                mainText = R.string.select_languauges,
                subText = R.string.phone_default,
                rightImg = R.drawable.ic_front_arrow
            )
            SettingsScreenCard(
                mainImg = R.drawable.ic_bell,
                mainText = R.string.notifications,
                subText = R.string.enable_recommended_notifications,
                rightImg = R.drawable.ic_front_arrow
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 15.sdp)
                .padding(top = 15.sdp)
        ) {


            Text(
                text = stringResource(R.string.about),
                fontSize = 20.ssp,
                fontWeight = FontWeight.Bold,
                color = MyColors.Green058
            )

            SettingsScreenCard(
                mainImg = R.drawable.ic_text,
                mainText = R.string.feedback,
                subText = R.string.let_us_know_your_experience_with_app,
                rightImg = R.drawable.ic_front_arrow
            )
            SettingsScreenCard(
                mainImg = R.drawable.ic_rateing,
                mainText = R.string.rate_us,
                subText = R.string.give_us_rating_how_much_you_like_app,
                rightImg = R.drawable.ic_front_arrow
            )
            SettingsScreenCard(
                mainImg = R.drawable.ic_sharing,
                mainText = R.string.share_app,
                subText = R.string.give_us_a_help_to_share_this_app,
                rightImg = R.drawable.ic_front_arrow
            )
            SettingsScreenCard(
                mainImg = R.drawable.ic_seacurity,
                mainText = R.string.privacy_policy,
                subText = R.string.learn_our_terms_conditions,
                rightImg = R.drawable.ic_front_arrow
            )
            SettingsScreenCard(
                mainImg = R.drawable.ic_version,
                mainText = R.string.version,
                subText = R.string._1_01,
                rightImg = R.drawable.ic_front_arrow
            )

        }
    }
}
@Composable
fun SettingsScreenCard(mainImg: Int,
                       mainText: Int,
                       subText: Int,
                       rightImg: Int,
                       onClick:()-> Unit={}) {
    Column(modifier = Modifier.padding(5.sdp)) {

        Card(
            onClick = onClick,
            colors = CardDefaults.cardColors(Color.White),
            shape = RoundedCornerShape(12.sdp),
            elevation = CardDefaults.cardElevation(2.sdp),
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .padding(8.sdp),
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
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(mainText),
                        fontSize = 14.ssp,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(subText),
                        fontSize = 12.ssp,
                        color = MyColors.greyD56_80
                    )
                }
                Image(
                    painter = painterResource(rightImg),
                    contentDescription = null
                )
            }
        }
    }
}



