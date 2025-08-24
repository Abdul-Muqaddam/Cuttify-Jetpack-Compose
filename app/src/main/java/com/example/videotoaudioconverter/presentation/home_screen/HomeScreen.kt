package com.example.videotoaudioconverter.presentation.home_screen


import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.home_screen.component.FeatureCard
import com.example.videotoaudioconverter.presentation.home_screen.component.MainText
import com.example.videotoaudioconverter.presentation.home_screen.component.VerticalSpacer
import com.example.videotoaudioconverter.presentation.main_screen.permission.VideoAndPhotoPermission
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun HomeScreen(navigateToSettingScreen: () -> Unit,
               navigateToVideToAudioConverter: () -> Unit,
               navigateToSetRingtoneScreen:()->Unit,
               navigateToPremiumScreen:()->Unit
) {
    val context = LocalContext.current
    var isPermissionForVideoAndImage by remember { mutableStateOf(false) }
    var isPermissionPermanentlyDenied by remember { mutableStateOf(false) }


    if (isPermissionForVideoAndImage) {
        VideoAndPhotoPermission(
            onGranted = {
                navigateToVideToAudioConverter()
            },
            onDeniedTemporarily = {
                isPermissionForVideoAndImage = false
            },
            onDeniedPermanently = {
                isPermissionPermanentlyDenied = true
                isPermissionForVideoAndImage = false
            },
        )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 18.sdp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.sdp, bottom = 20.sdp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                fontSize = 26.ssp,
                text = stringResource(R.string.cuttify),
                color = MyColors.MainColor
            )
            Row {
                Image(
                    modifier = Modifier
                        .size(28.sdp)
                        .padding(end = 8.sdp)
                        .clickable() {
                            navigateToSettingScreen()
                        },
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier.size(24.sdp)
                        .clickable {
                            navigateToPremiumScreen()
                        },
                    painter = painterResource(R.drawable.ic_diamond),
                    contentDescription = null
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable() {
                    isPermissionForVideoAndImage = true
                }, contentAlignment = Alignment.Center
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
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier.size(36.sdp),
                        painter = painterResource(R.drawable.ic_video),
                        contentDescription = null
                    )
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 8.sdp)
                            .size(16.sdp),
                        painter = painterResource(R.drawable.ic_right_double_arrow),
                        contentDescription = null
                    )
                    Image(
                        modifier = Modifier.size(36.sdp),
                        painter = painterResource(R.drawable.ic_volumn), contentDescription = null
                    )
                }
                Text(
                    fontSize = 14.ssp,
                    text = stringResource(R.string.video_to_audio_converter),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        VerticalSpacer(20)
        MainText(text = stringResource(R.string.audio))
        VerticalSpacer(16)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FeatureCard(
                imgWidth = 30,
                imgHeight = 30,
                img = R.drawable.ic_audio__player,
                text = stringResource(R.string.audio_player),
                onClick = {}
            )
            FeatureCard(
                imgWidth = 50,
                imgHeight = 30,
                img = R.drawable.ic_audio_cutter,
                text = stringResource(R.string.audio_cutter),
                onClick = {}
            )
            FeatureCard(
                imgWidth = 65,
                imgHeight = 30,
                img = R.drawable.ic_audio_merge,
                text = stringResource(R.string.audio_merge),
                onClick = {}
            )
        }
        VerticalSpacer(20)
        MainText(text = stringResource(R.string.video))
        VerticalSpacer(16)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FeatureCard(
                imgWidth = 30,
                imgHeight = 30,
                img = R.drawable.ic_video_cutter,
                text = stringResource(R.string.video_cutter),
                onClick = {}
            )
            FeatureCard(
                imgWidth = 50,
                imgHeight = 30,
                img = R.drawable.ic_video_player,
                text = stringResource(R.string.video_player),
                onClick = {}
            )
            FeatureCard(
                imgWidth = 65,
                imgHeight = 30,
                img = R.drawable.ic_ringtoon,
                text = stringResource(R.string.set_ringtone),
                onClick = {navigateToSetRingtoneScreen()}
            )
        }
    }
    if (isPermissionPermanentlyDenied) {
        Dialog(

            onDismissRequest = {
                isPermissionPermanentlyDenied = false
            }
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.sdp)
                    )
                    .border(
                        width = 2.sdp,
                        color = MyColors.Green058,
                        shape = RoundedCornerShape(10.sdp)
                    )
                    .padding(16.sdp)
            ) {
                Text(
                    text = stringResource(R.string.permission_denied),
                    fontSize = 18.ssp,
                    color = MyColors.Green058
                )
                VerticalSpacer(10)
                Text(text = stringResource(R.string.some_permissions_were_permanently_denied_please_enable_them_from_app_settings))
                VerticalSpacer(10)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = stringResource(R.string.cancel),
                        modifier = Modifier
                            .padding(end = 8.sdp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {
                                isPermissionPermanentlyDenied = false
                            })
                    Text(
                        text = stringResource(R.string.go_to_settings),
                        color = MyColors.Green058,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            try {
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = android.net.Uri.fromParts(
                                            "package",
                                            context.packageName,
                                            null
                                        )
                                    }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Unable to open settings",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            isPermissionPermanentlyDenied = false
                        },
                    )
                }
            }
        }
    }
}

