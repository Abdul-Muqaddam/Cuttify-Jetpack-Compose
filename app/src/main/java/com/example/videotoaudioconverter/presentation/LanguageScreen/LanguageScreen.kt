package com.example.videotoaudioconverter.presentation.LanguageScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


@Composable
fun LanguageScreen(

){
    var searchQuery by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(horizontal = 15.sdp)) {
        Row(
            modifier = Modifier.padding(top = 25.sdp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_baseline_arrow_back),
                contentDescription = null,
                modifier = Modifier
                    .width(22.sdp)
                    .height(22.sdp)

            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Languages",
                fontSize = 22.ssp,
                color = MyColors.Green058
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.ic_tick),
                contentDescription = null,
                modifier = Modifier
                    .width(22.sdp)
                    .height(22.sdp)
            )
        }
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it

            }
        )

        LanguageScreenCard(
            mainText = R.string.english,
            subText = (R.string.english)
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String = "Search by Country"
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null
            )
        },
        placeholder = {
            Text(text = placeholderText)
        },
        singleLine = true,
        shape = RoundedCornerShape(12.sdp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.sdp)
    )

}
@Composable
fun LanguageScreenCard(
                       mainText: Int,
                       subText: Int,
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
                    Text(
                        text = stringResource(mainText),
                        fontSize = 14.ssp,
                        color = Color.Black
                    )
                Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(subText),
                        fontSize = 12.ssp,
                        color = MyColors.greyD56_80
                    )

            }
        }
    }
}
