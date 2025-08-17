package com.example.videotoaudioconverter.presentation.LanguageScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.ssp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ir.kaaveh.sdpcompose.sdp


@Composable
fun LanguageScreenComponent(model: LanguagesModel, languageSelected: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.sdp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.sdp)
            .clickable {  },
        elevation = CardDefaults.cardElevation(0.sdp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
            Row(
                modifier = Modifier
                    .padding(8.sdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.languageName,
                    fontSize = 14.ssp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = model.nativeName,
                    fontSize = 12.ssp,
                    color = MyColors.greyD56_80
                )

            }

    }
}
