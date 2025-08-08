package com.example.videotoaudioconverter.presentation.LanguageScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.LinkAnnotation
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


@Composable
fun LanguageScreen(
    navigateBackToSettingScreen: () -> Unit

) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(R.string.english) }
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
                    .clickable { navigateBackToSettingScreen() }

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

        Spacer(modifier = Modifier.height(10.sdp))

        val languages = listOf(
            R.string.english to R.string.english1,
            R.string.german to R.string.deutsch,
            R.string.french to R.string.fran_ais,
            R.string.japan to R.string.j,
            R.string.korean to R.string.k,
            R.string.italian to R.string.italiano,
            R.string.arabic to R.string.a
        )


        languages.forEach { (titleRes, subtitleRes) ->
            LanguageItemCard(
                title = titleRes,
                subtitle = subtitleRes,
                isSelected = selectedLanguage == titleRes, // Highlight only if selected
                onClick = {
                    selectedLanguage = titleRes // Update selected item
                }
            )
        }

//        LanguageItemCard(
//            title = R.string.english,
//            subtitle = R.string.english1,
//            isSelected = true
//        )
//
//        LanguageItemCard(
//            title = R.string.english,
//            subtitle = R.string.english1,
//            isSelected = false
//        )
//
//        LanguageItemCard(
//            title = R.string.german,
//            subtitle = R.string.deutsch,
//            isSelected = false
//        )
//
//        LanguageItemCard(
//            title = R.string.french,
//            subtitle = R.string.fran_ais,
//            isSelected = false
//        )
//
//        LanguageItemCard(
//            title = R.string.japan,
//            subtitle = R.string.j,
//            isSelected = false
//        )
//
//        LanguageItemCard(
//            title = R.string.korean,
//            subtitle = R.string.k,
//            isSelected = false
//        )
//
//        LanguageItemCard(
//            title = R.string.italian,
//            subtitle = R.string.italiano,
//            isSelected = false
//        )
//
//        LanguageItemCard(
//            title = R.string.arabic,
//            subtitle = R.string.a,
//            isSelected = false
//        )


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
fun LanguageItemCard(
    title: Int,
    subtitle: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MyColors.Green058 else Color.Transparent

    Card(
        shape = RoundedCornerShape(16.sdp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.sdp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(0.sdp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.sdp,
                    color = borderColor,
                    shape = RoundedCornerShape(16.sdp)
                )
                .padding(horizontal = 16.sdp, vertical = 10.sdp)
        ) {
            Row(
                modifier = Modifier
                    .padding(8.sdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(title),
                    fontSize = 14.ssp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(subtitle),
                    fontSize = 12.ssp,
                    color = MyColors.greyD56_80
                )

            }
        }
    }
}


//@Composable
//fun LanguageScreenCard(
//                       mainText: Int,
//                       subText: Int,
//                       onClick:()-> Unit={}) {
//    Column(modifier = Modifier.padding(5.sdp)) {
//
//        Card(
//            onClick = onClick,
//            colors = CardDefaults.cardColors(Color.White),
//            shape = RoundedCornerShape(12.sdp),
//            elevation = CardDefaults.cardElevation(2.sdp),
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//
//            Row(
//                modifier = Modifier
//                    .padding(8.sdp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                    Text(
//                        text = stringResource(mainText),
//                        fontSize = 14.ssp,
//                        color = Color.Black
//                    )
//                Spacer(modifier = Modifier.weight(1f))
//                    Text(
//                        text = stringResource(subText),
//                        fontSize = 12.ssp,
//                        color = MyColors.greyD56_80
//                    )
//
//            }
//        }
//    }
//}
