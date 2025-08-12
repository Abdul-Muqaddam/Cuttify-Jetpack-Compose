package com.example.videotoaudioconverter.presentation.comman_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.home_screen.component.VerticalSpacer
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    sortFilter: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: BottomSheetViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    ModalBottomSheet(containerColor = Color.White, onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.sdp)
        ) {
            Text(
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.sdp), text = stringResource(
                    R.string.sort_by
                ), fontSize = 18.ssp, color = MyColors.MainColor
            )
            VerticalSpacer(4)
            Text(
                modifier = Modifier.padding(horizontal = 10.sdp), text = stringResource(
                    R.string.order
                ), fontSize = 16.ssp, color = MyColors.MainColor
            )
            VerticalSpacer(4)

            if (state.isDateSelected) {
                BottomSheetComponent(
                    selected = state.selectedDateOrder == "new_to_old",
                    text = stringResource(R.string.new_to_old),
                    onClick = {
                        sortFilter("new_to_old")
                        viewModel.setDateOrder("new_to_old")
                    })
                BottomSheetComponent(
                    selected = state.selectedDateOrder == "old_to_new",
                    text = stringResource(R.string.old_to_new),
                    onClick = {
                        sortFilter("old_to_new")
                        viewModel.setDateOrder("old_to_new")
                    })
            }

            if (state.isSizeSelected) {
                BottomSheetComponent(
                    selected = state.selectedSizeOrder == "Small_To_Large",
                    text = stringResource(R.string.small_to_large),
                    onClick = {
                        sortFilter("Small_To_Large")
                        viewModel.setSizeOrder("Small_To_Large")
                    })
                BottomSheetComponent(
                    selected = state.selectedSizeOrder == "Large_To_Small",
                    text = stringResource(R.string.large_to_small),
                    onClick = {
                        sortFilter("Large_To_Small")
                        viewModel.setSizeOrder("Large_To_Small")
                    })
            }

            if (state.isDurationSelected) {
                BottomSheetComponent(
                    selected = state.selectedDurationOrder == "Short_to_Long",
                    text = stringResource(R.string.new_to_old),
                    onClick = {
                        sortFilter("Short_to_Long")
                        viewModel.setDurationOrder("Short_to_Long")
                    })
                BottomSheetComponent(
                    selected = state.selectedDurationOrder == "Long_to_Short",
                    text = stringResource(R.string.old_to_new),
                    onClick = {
                        sortFilter("Long_to_Short")
                        viewModel.setDurationOrder("Long_to_Short")
                    })
            }
            if (state.isNameSelected) {
                BottomSheetComponent(
                    selected = state.selectedNameOrder == "A_to_Z",
                    text = stringResource(R.string.a_to_z),
                    onClick = {
                        sortFilter("A_to_Z")
                        viewModel.setNameOrder("A_to_Z")
                    })
                BottomSheetComponent(
                    selected = state.selectedNameOrder == "Z_to_A",
                    text = stringResource(R.string.z_to_a),
                    onClick = {
                        sortFilter("Z_to_A")
                        viewModel.setNameOrder("Z_to_A")
                    })
            }


            VerticalSpacer(4)
            Column(
                modifier = Modifier
                    .height(2.sdp)
                    .fillMaxWidth()
                    .background(MyColors.MainColor)
            ) {}
            VerticalSpacer(4)
            BottomSheetComponent(
                selected = state.selectedSortBy == "Date",
                text = stringResource(R.string.date),
                onClick = {
                    viewModel.selectSortBy("Date")
                    viewModel.setIsDurationSelected(false)
                    viewModel.setIsNameSelected(false)
                    viewModel.setIsSizeSelected(false)
                    viewModel.setIsDateSelected(true)
                })
            VerticalSpacer(4)
            BottomSheetComponent(
                selected = state.selectedSortBy == "size",
                text = stringResource(R.string.size),
                onClick = {
                    viewModel.selectSortBy("size")
                    viewModel.setIsDurationSelected(false)
                    viewModel.setIsNameSelected(false)
                    viewModel.setIsSizeSelected(true)
                    viewModel.setIsDateSelected(false)
                })
            VerticalSpacer(4)
            BottomSheetComponent(
                selected = state.selectedSortBy == "Duration",
                text = stringResource(R.string.duration),
                onClick = {
                    viewModel.selectSortBy("Duration")
                    viewModel.setIsDurationSelected(true)
                    viewModel.setIsNameSelected(false)
                    viewModel.setIsSizeSelected(false)
                    viewModel.setIsDateSelected(false)

                })
            VerticalSpacer(4)
            BottomSheetComponent(
                selected = state.selectedSortBy == "Name",
                text = stringResource(R.string.name),
                onClick = {
                    viewModel.selectSortBy("Name")
                    viewModel.setIsDurationSelected(false)
                    viewModel.setIsNameSelected(true)
                    viewModel.setIsSizeSelected(false)
                    viewModel.setIsDateSelected(false)
                })
        }
    }
}

@Composable
fun BottomSheetComponent(selected: Boolean, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable() {
                onClick()
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            colors = RadioButtonDefaults.colors(
                selectedColor = MyColors.MainColor,
                unselectedColor = Color.Gray
            ), selected = selected, onClick = { onClick() })
        Text(color = MyColors.MainColor, text = text)
    }
}