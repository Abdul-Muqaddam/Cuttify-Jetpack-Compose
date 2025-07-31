package com.example.videotoaudioconverter.presentation.home_screen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun FeatureCard(imgWidth: Int, imgHeight: Int, img: Int, text: String) {

    Column(
        modifier = Modifier
            .shadow(elevation = 5.sdp, shape = RoundedCornerShape(10.sdp))
            .background(color = Color.White)
            .border(
                width = 1.sdp,
                color = MyColors.Green058,
                shape = RoundedCornerShape(10.sdp)
            )
            .padding(vertical = 18.sdp, horizontal = 6.sdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(height = imgHeight.sdp, width = imgWidth.sdp),
            painter = painterResource(img),
            contentDescription = null
        )
        Text(
            fontSize = 12.ssp,
            text = text,
            color = MyColors.Green058
        )
    }
}