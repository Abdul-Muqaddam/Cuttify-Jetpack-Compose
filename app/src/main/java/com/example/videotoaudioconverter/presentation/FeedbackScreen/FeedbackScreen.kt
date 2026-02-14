package com.example.videotoaudioconverter.presentation.feedbackScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import android.content.pm.PackageManager



@Composable
fun FeedbackScreen(navigateBackToSettingsScreen:()->Unit) {
    var isConnectionProblemChecked by remember { mutableStateOf(false) }
    var isAppNotWorkingChecked by remember { mutableStateOf(false) }
    var isLoadingTooMuchChecked by remember { mutableStateOf(false) }
    var isSuggestAppChecked by remember { mutableStateOf(false) }
    var descriptionText by remember { mutableStateOf("") }
    val context = LocalContext.current
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
                    .clickable { navigateBackToSettingsScreen() }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.feedback),
                fontSize = 22.ssp,
                color = MyColors.Green058,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Text(
            modifier = Modifier.padding(top = 30.sdp),
            text = "Please Select the Type of Feedback",
            fontSize = 16.ssp
        )

        Spacer(modifier = Modifier.height(10.sdp))

        FeedbackCheckboxRow(
            text = stringResource(R.string.connection_problem),
            isChecked = isConnectionProblemChecked,
            onCheckedChange = { isConnectionProblemChecked = it }
        )

        FeedbackCheckboxRow(
            text = stringResource(R.string.app_not_working),
            isChecked = isAppNotWorkingChecked,
            onCheckedChange = { isAppNotWorkingChecked = it }
        )

        FeedbackCheckboxRow(
            text = stringResource(R.string.app_take_too_much_time_on_loading),
            isChecked = isLoadingTooMuchChecked,
            onCheckedChange = { isLoadingTooMuchChecked = it }
        )

        FeedbackCheckboxRow(
            text = stringResource(R.string.suggest_your_favorite_apps_others),
            isChecked = isSuggestAppChecked,
            onCheckedChange = { isSuggestAppChecked = it }
        )

        Text(
            text = "Description",
            fontSize = 16.ssp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 20.sdp, bottom = 8.sdp)
        )

        OutlinedTextField(
            value = descriptionText,
            onValueChange = { descriptionText = it },
            placeholder = { Text(text = "Write suggestions here ....") },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.sdp),
            maxLines = 10,
            shape = RoundedCornerShape(10.sdp),
            )
        Button (
            onClick = {
                val packageManager = context.packageManager
                val packageName = context.packageName
                val version = try {
                    packageManager.getPackageInfo(packageName, 0).versionName ?: "Unknown"
                } catch (e: PackageManager.NameNotFoundException) {
                    "Unknown"
                }

                val osVersion = Build.VERSION.SDK_INT
                val deviceModel = Build.MODEL
                val deviceCode = Build.DEVICE

                val selectedIssues = buildList {
                    if (isConnectionProblemChecked) add("• Connection Problem")
                    if (isAppNotWorkingChecked) add("• App Not Working")
                    if (isLoadingTooMuchChecked) add("• Loading Too Much")
                    if (isSuggestAppChecked) add("• Suggest App")
                }.joinToString("\n")

                val body = buildString {
                    append("Feedback for Cuttify\n\n")

                    append("App Name: Cuttify\n")
                    append("App Version: $version\n")
                    append("Device Model: $deviceModel\n")
                    append("Device Code: $deviceCode\n")
                    append("OS Version: $osVersion\n\n")

                }
                val body2 = buildString {
                    append("Selected Issues:\n")
                    append(if (selectedIssues.isNotEmpty()) "$selectedIssues\n\n" else "None\n\n")

                    append("User Feedback:\n")
                    append(if (descriptionText.isNotBlank()) descriptionText else "No feedback provided.")
                }

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("abdul.muqaddam.se@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, body)
                    putExtra(Intent.EXTRA_TEXT, body2)
                }

                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(20.sdp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MyColors.Green058,
                contentColor = Color.White,
            ),
            modifier = Modifier
                .padding(top = 30.sdp)
                .padding(horizontal = 20.sdp)
                .fillMaxWidth()
                .height(45.sdp)
        ) {
            Text(text = "Submit",
                fontWeight = FontWeight.Bold,
                fontSize = 16.ssp
            )
        }
    }
}

@Composable
fun FeedbackCheckboxRow(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MyColors.Green058,
                checkmarkColor = Color.White,
                uncheckedColor = MyColors.Green058
            )
        )
        Spacer(modifier = Modifier.width(1.sdp))
        Text(
            text = text,
            fontSize = 14.ssp,
            color = MyColors.greyD56_80
        )
    }
}
