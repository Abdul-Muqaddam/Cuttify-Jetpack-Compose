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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun SetRingtoneScreen(navigateBackToMainScreen:()->Unit) {
    var showPermissionDialog by remember { mutableStateOf(false) }
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
            onClick = { showPermissionDialog = true }
        )

        SetRingtoneScreenCard(
            mainImg = R.drawable.ic_bell_fill,
            mainText = R.string.notification,
            rightImg = R.drawable.ic_front_arrow
        )

        SetRingtoneScreenCard(
            mainImg = R.drawable.ic_alarm_fill,
            mainText = R.string.alarm,
            rightImg = R.drawable.ic_front_arrow
        )

    }
    if (showPermissionDialog) {
        PermissionDialog(
            onAllow = {
                showPermissionDialog = false
                // TODO: Here request WRITE_SETTINGS permission
            },
            onDismiss = { showPermissionDialog = false }
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
@Composable
fun PermissionDialog(
    onAllow: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.sdp),
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(4.sdp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.sdp)
        ) {
            Column(
                modifier = Modifier.padding(20.sdp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_telephone_ring),
                    contentDescription = null,
                    modifier = Modifier.size(60.sdp)
                )
                Spacer(modifier = Modifier.height(15.sdp))
                Text(
                    text = "To set ringtones, we need your permission.",
                    fontSize = 16.ssp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.sdp))
                Button(
                    onClick = onAllow,
                    shape = RoundedCornerShape(10.sdp),
                    colors = ButtonDefaults.buttonColors(MyColors.Green058),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Allow", color = Color.White, fontSize = 16.ssp)
                }
                Spacer(modifier = Modifier.height(10.sdp))
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.sdp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MyColors.Green058),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Not now", fontSize = 16.ssp)
                }
            }
        }
    }
}
