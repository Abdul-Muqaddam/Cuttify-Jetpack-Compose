package com.example.videotoaudioconverter.presentation.all_video_files.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun TopBarFilter(
    totalItems:Int,
    filterClicked: (String) -> Unit
){
    var expanded by remember { mutableStateOf(false) }
    val options: List<String>  = listOf("A_to_Z", "Z_to_A", "new_to_old", "old_to_new", "Small_To_Large", "Large_To_Small")
    Row(
        modifier = Modifier
            .padding(vertical = 10.sdp, horizontal = 16.sdp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$totalItems items", color = MyColors.Green058, fontSize = 18.ssp)
       Box{
           Image(
            modifier = Modifier.size(22.sdp).clickable{ expanded = true },
            painter = painterResource(R.drawable.ic_filter),
            contentDescription = null
        )
           DropdownMenu(
               expanded = expanded,
               onDismissRequest = { expanded = false }
           ) {
               options.forEach { option ->
                   DropdownMenuItem(
                       text = { Text(option.replace("_", " ")) },
                       onClick = {
                           filterClicked(option)
                           expanded = false
                       }
                   )
               }
           }
        }
    }
}


