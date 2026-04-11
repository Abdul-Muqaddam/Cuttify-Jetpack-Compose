package com.example.videotoaudioconverter.presentation.premiumScreen

import android.app.Activity
import android.content.Context
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.Premium_Screen.BillingHelper
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp



@Composable
fun Premium_Screen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var isPremium by remember { mutableStateOf(prefs.getBoolean("is_premium", false)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.sdp)
                .padding(top = 40.sdp)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
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

            premiumtitle(img = R.drawable.ic_tick, text = stringResource(R.string.even_higher_quality))
            premiumtitle(img = R.drawable.ic_tick, text = stringResource(R.string.remove_ads))
            premiumtitle(img = R.drawable.ic_tick, text = stringResource(R.string.completely_unlimited_access))
            premiumtitle(img = R.drawable.ic_tick, text = stringResource(R.string.instant_support))
            premiumtitle(img = R.drawable.ic_tick, text = stringResource(R.string.and_much_more))

            if (isPremium) {
                // ── Already Premium ────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.sdp)
                        .height(40.sdp)
                        .padding(horizontal = 10.sdp)
                        .background(
                            color = MyColors.Green058.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.sdp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓ You are Premium!",
                        color = MyColors.Green058,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.ssp
                    )
                }
            } else {
                // ── Subscribe Button ───────────────────────────────
                Button(
                    onClick = {
                        prefs.edit().putBoolean("is_premium", true).apply()
                        isPremium = true
                    },
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
            }

            Text(
                text = stringResource(R.string.auto_renewal_and_cancel_anytime),
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
    text: String
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
            text = text,
            fontSize = 12.ssp,
            color = MyColors.Green226
        )
    }
}