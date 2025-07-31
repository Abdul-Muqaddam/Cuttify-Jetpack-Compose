package com.example.videotoaudioconverter.presentation.setting_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.videotoaudioconverter.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun SettingScreen() {
    Column{
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 35.sdp),
            ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(R.drawable.ic_main_gradient),
                contentDescription = null
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Image(painter = painterResource(R.drawable.ic_crown),contentDescription = null,
                        modifier = Modifier
                            .width(65.sdp)
                            .height(65.sdp))
                    Column {
                        Text(text = stringResource(R.string.upgrade_premium))
                    }
                }
            }
        }
    }
}