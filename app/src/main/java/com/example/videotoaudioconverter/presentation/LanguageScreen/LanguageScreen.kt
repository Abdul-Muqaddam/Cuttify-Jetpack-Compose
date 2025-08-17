//package com.example.videotoaudioconverter.presentation.LanguageScreen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import com.example.videotoaudioconverter.R
//import com.example.videotoaudioconverter.data.AppPreference
//import com.example.videotoaudioconverter.ui.theme.MyColors
//import ir.kaaveh.sdpcompose.sdp
//import ir.kaaveh.sdpcompose.ssp
//import org.koin.androidx.compose.koinViewModel
//
//@Composable
//fun LanguageScreen(
//    navigateBckToSettingScreen: () -> Unit,
//    viewModel: LanguageScreenViewModel = koinViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//    var searchQuery by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(top = 30.sdp, start = 15.sdp, end = 15.sdp)
//    ) {
//        // Top bar
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(20.sdp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(R.drawable.ic_baseline_arrow_back),
//                contentDescription = null,
//                modifier = Modifier
//                    .width(18.sdp)
//                    .height(13.sdp)
//                    .clickable { navigateBckToSettingScreen() }
//            )
//
//            Text(
//                text = stringResource(R.string.select_languauges),
//                fontSize = 18.ssp,
//                modifier = Modifier.padding(start = 5.sdp)
//            )
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            Row(
//                modifier = Modifier.clickable {
//                    AppPreference.setLanguageCode(state.selectedLanguage)
//                },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Image(
//                    contentDescription = null,
//                    painter = painterResource(R.drawable.ic_tick),
//                    modifier = Modifier.padding(start = 5.sdp)
//                )
//            }
//        }
//
//        // Search bar
//        SearchBar(
//            query = searchQuery,
//            onQueryChange = {
//                searchQuery = it
//                viewModel.onLanguageSearch(it)
//            }
//        )
//
//        // Selected language card
//        val selectedLanguage by remember(state) {
//            derivedStateOf {
//                state.languages.firstOrNull { it.isSelected }
//            }
//        }
//
//        selectedLanguage?.let { selectLanguage ->
//            Card(
//                colors = CardDefaults.cardColors(Color.White),
//                shape = RoundedCornerShape(12.sdp),
//                elevation = CardDefaults.cardElevation(2.sdp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 10.sdp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(12.sdp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = selectLanguage.languageName,
//                        fontSize = 14.ssp,
//                        color = Color.Black
//                    )
//                    Spacer(modifier = Modifier.weight(1f))
//                    Text(
//                        text = selectLanguage.nativeName,
//                        fontSize = 12.ssp,
//                        color = MyColors.greyD56_80
//                    )
//                }
//            }
//        }
//
//        // Title
//        Text(
//            modifier = Modifier.padding(top = 15.sdp, bottom = 15.sdp),
//            text = "All Languages",
//            color = MyColors.green841,
//            fontSize = 15.ssp
//        )
//
//        // Scrollable language grid
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            items(state.languages) { model ->
//                LanguageScreenComponent(
//                    model = model,
//                    languageSelected = {
//                        viewModel.onLanguageSelect(it)
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun SearchBar(
//    query: String,
//    onQueryChange: (String) -> Unit,
//    placeholderText: String = "Search by Country"
//) {
//    OutlinedTextField(
//        value = query,
//        onValueChange = onQueryChange,
//        leadingIcon = {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_search),
//                contentDescription = null
//            )
//        },
//        placeholder = {
//            Text(text = placeholderText)
//        },
//        singleLine = true,
//        shape = RoundedCornerShape(12.sdp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 15.sdp)
//    )
//}



package com.example.videotoaudioconverter.presentation.LanguageScreen

import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import org.koin.androidx.compose.koinViewModel

@Composable
fun LanguageScreen(
    navigateBack: () -> Unit,
    viewModel: LanguageScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.sdp, start = 15.sdp, end = 15.sdp)
    ) {
        // Top row: back + title + apply tick
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_back),
                contentDescription = null,
                modifier = Modifier
                    .width(24.sdp)
                    .height(24.sdp)
                    .clickable { navigateBack() }
            )

            Text(
                text = stringResource(R.string.select_languauges),
                modifier = Modifier.padding(start = 12.sdp),
                fontSize = 18.ssp,
                color = MyColors.Green058
            )

            Spacer(modifier = Modifier.weight(1f))

            // Apply button (you already save on select, so this can be used to confirm / go back)
            Icon(
                painter = painterResource(id = R.drawable.ic_tick),
                contentDescription = null,
                modifier = Modifier
                    .width(24.sdp)
                    .height(24.sdp)
                    .clickable {
                        // optional: show toast and go back
                        Toast.makeText(ctx, "Language applied", Toast.LENGTH_SHORT).show()
                        navigateBack()
                    }
            )
        }

        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                viewModel.onLanguageSearch(it)
            }
        )

        // Selected item preview (optional)
        val selectedLanguage = state.languages.firstOrNull { it.isSelected }
        selectedLanguage?.let { sel ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.sdp),
                shape = RoundedCornerShape(12.sdp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.sdp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = sel.languageName, fontSize = 14.ssp, color = Color.Black)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = sel.nativeName, fontSize = 12.ssp, color = MyColors.greyD56_80)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.sdp))

        // Title
        Text(
            modifier = Modifier.padding(bottom = 6.sdp),
            text = "All Languages",
            color = MyColors.green841,
            fontSize = 15.ssp
        )

        // Single-column scrollable list (one item per row)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.sdp)
        ) {
            items(state.languages) { model ->
                LanguageItem(
                    model = model,
                    isSelected = model.isSelected,
                    onClick = {
                        Log.d("LanguageScreen", "Clicked ${model.shortCode}")
                        viewModel.onLanguageSelect(it)
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageItem(
    model: LanguagesModel,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(model.shortCode) },
        shape = RoundedCornerShape(12.sdp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEFFAF0) else Color.White
        ),
        border = if (isSelected) BorderStroke(1.sdp, MyColors.Green058) else null,
        elevation = CardDefaults.cardElevation(0.sdp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {

                Text(text = model.languageName, fontSize = 16.ssp, color = Color.Black)
            Spacer(modifier = Modifier.weight(1f))
                Text(text = model.nativeName, fontSize = 12.ssp, color = MyColors.greyD56_80)

        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null)
        },
        placeholder = { Text(text = "Search languages") },
        singleLine = true,
        shape = RoundedCornerShape(12.sdp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.Gray,
            cursorColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLeadingIconColor = Color.Gray,
            unfocusedLeadingIconColor = Color.Gray,
            focusedPlaceholderColor = MyColors.greyD56_80,
            unfocusedPlaceholderColor = MyColors.greyD56_80
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.sdp)
    )
}
