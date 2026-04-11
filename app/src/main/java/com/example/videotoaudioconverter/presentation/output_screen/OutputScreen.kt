package com.example.videotoaudioconverter.presentation.outputScreen

import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.videotoaudioconverter.R
import com.example.videotoaudioconverter.presentation.adsMob.BannerAd
import com.example.videotoaudioconverter.presentation.output_screen.FileOpener
import com.example.videotoaudioconverter.ui.theme.MyColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import java.io.File

@Composable
fun OutPutScreen() {
    val context = LocalContext.current
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember(searchQuery) { searchFiles(searchQuery) }
    Column(modifier = Modifier.padding(horizontal = 10.sdp)) {
        if (isSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.sdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    isSearchActive = false
                    searchQuery = ""
                }) {
                    Image(
                        painterResource(R.drawable.ic_front_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(20.sdp)
                    )
                }
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(text = "Search by name or size...", color = Color.Gray)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MyColors.MainColor,
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Output files", fontSize = 20.ssp)
                Spacer(modifier = Modifier.weight(0.7f))
                IconButton(onClick = { isSearchActive = true }) {
                    Image(
                        painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                }
                Spacer(modifier = Modifier.weight(0.1f))
            }
        }

        Row() {
            Text(
                text = "RECENT",
                color = Color.Gray
            )
            Spacer(modifier = Modifier.weight(1f))
            Row() {
                Text(
                    text = "View All",
                    color = Color.Gray
                )
            }
        }

        val (usedFormatted, totalFormatted, percent) = remember { getAppStorageInfo() }

        Card(
            modifier = Modifier
                .padding(top = 5.sdp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.sdp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(4.sdp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.sdp)
            ) {
                Column {
                    // ── Top row: title + badge ──────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "APP STORAGE",
                            color = Color(0xFF888888),
                            fontSize = 10.ssp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.5.sp
                        )
                        Card(
                            shape = RoundedCornerShape(20.sdp),
                            colors = CardDefaults.cardColors(
                                containerColor = MyColors.MainColor.copy(alpha = 0.12f)
                            )
                        ) {
                            Text(
                                text = "CUTTIFY",
                                modifier = Modifier.padding(horizontal = 8.sdp, vertical = 3.sdp),
                                color = MyColors.MainColor,
                                fontSize = 9.ssp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.sdp))

                    // ── Middle: big percent + used/total ───────────────────
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$percent%",
                            color = MyColors.MainColor,
                            fontSize = 36.ssp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.sdp))
                        Column(modifier = Modifier.padding(bottom = 4.sdp)) {
                            Text(
                                text = usedFormatted,
                                color = Color(0xFF333333),
                                fontSize = 11.ssp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "of $totalFormatted",
                                color = Color(0xFF888888),
                                fontSize = 10.ssp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.sdp))

                    // ── Progress bar ───────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.sdp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFDDDDDD))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (percent.toFloat() / 100f).coerceIn(0f, 1f))
                                .height(6.sdp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(MyColors.MainColor, Color(0xFF00C97A))
                                    )
                                )
                        )
                    }
                }
            }
        }

        if (isSearchActive && searchQuery.isNotBlank()) {
            Text(
                modifier = Modifier.padding(top = 5.sdp),
                text = "${searchResults.size} results found",
                color = Color.Gray
            )
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.sdp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No files found", color = Color.Gray, fontSize = 14.ssp)
                }
            } else {
                searchResults.forEach { (fileName, fileSize, filePath) ->
                    Column(modifier = Modifier.padding(5.sdp)) {
                        Card(
                            colors = CardDefaults.cardColors(Color.White),
                            shape = RoundedCornerShape(12.sdp),
                            elevation = CardDefaults.cardElevation(2.sdp),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                FileOpener.openFolder(context, File(filePath).parent ?: filePath)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.sdp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_music_card),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 10.sdp)
                                        .size(24.sdp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fileName,
                                        fontSize = 12.ssp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = fileSize,
                                        fontSize = 10.ssp,
                                        color = MyColors.greyD56_80
                                    )
                                }
                                Image(
                                    painter = painterResource(R.drawable.ic_front_arrow),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                modifier = Modifier.padding(top = 5.sdp),
                text = "Folders",
                color = Color.Gray
            )
            OutputScreenCard(
                mainImg = R.drawable.ic_music_card,
                mainText = "Audio Converted Files",
                subText = getFolderInfo("/storage/emulated/0/Music/Cuttify/VideoToAudio"),
                rightImg = R.drawable.ic_front_arrow,
                onClick = {
                    FileOpener.openFolder(context, "/storage/emulated/0/Music/Cuttify/VideoToAudio")
                }
            )
            OutputScreenCard(
                mainImg = R.drawable.ic_video_cutter,
                mainText = "Video Cutter Files",
                subText = getFolderInfo("/storage/emulated/0/Movies/Cuttify/Trimmed Video"),
                rightImg = R.drawable.ic_front_arrow,
                onClick = {
                    FileOpener.openFolder(context, "/storage/emulated/0/Movies/Cuttify/Trimmed Video")
                }
            )
            OutputScreenCard(
                mainImg = R.drawable.ic_audio_merge,
                mainText = "Audio Merge Files",
                subText = getFolderInfo("/storage/emulated/0/Music/Cuttify/MergedAudio"),
                rightImg = R.drawable.ic_front_arrow,
                onClick = {
                    FileOpener.openFolder(context, "/storage/emulated/0/Music/Cuttify/MergedAudio")
                }
            )
            OutputScreenCard(
                mainImg = R.drawable.ic_audio_cutter,
                mainText = "Audio Cutter Files",
                subText = getFolderInfo("/storage/emulated/0/Music/Cuttify/Trimmed Audio"),
                rightImg = R.drawable.ic_front_arrow,
                onClick = {
                    FileOpener.openFolder(context, "/storage/emulated/0/Music/Cuttify/Trimmed Audio")
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        BannerAd()
    }
}


@Composable
fun OutputScreenCard(
    mainImg: Int,
    mainText: String,
    subText: String? = null,
    subTextText: String? = null,
    rightImg: Int,
    onClick: () -> Unit = {}
) {
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
                Image(
                    painter = painterResource(mainImg),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.sdp)
                        .size(24.sdp)
                        .align(Alignment.CenterVertically)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = mainText,
                        fontSize = 12.ssp,
                        color = Color.Black
                    )
//                    Text(
//                        text = stringResource(subText),
//                        fontSize = 10.ssp,
//                        color = MyColors.greyD56_80
//                    )
                    when {
                        subText != null -> {
                            Text(
                                text = subText,
                                fontSize = 10.ssp,
                                color = MyColors.greyD56_80
                            )
                        }

                        subTextText != null -> {
                            Text(
                                text = subTextText,
                                fontSize = 10.ssp,
                                color = MyColors.greyD56_80
                            )
                        }
                    }
                }
                Image(
                    painter = painterResource(rightImg),
                    contentDescription = null
                )
            }
        }
    }
}


fun getFolderInfo(folderPath: String): String {
    val folder = File(folderPath)
    if (!folder.exists()) return "No files"

    val files = folder.walkBottomUp().filter { it.isFile }.toList()
    if (files.isEmpty()) return "No files"

    val totalSize = files.sumOf { it.length() }
    val formattedSize = when {
        totalSize >= 1_073_741_824L -> "%.1f GB".format(totalSize / 1_073_741_824.0)
        totalSize >= 1_048_576L     -> "%.1f MB".format(totalSize / 1_048_576.0)
        totalSize >= 1_024L         -> "%.1f KB".format(totalSize / 1_024.0)
        else                        -> "$totalSize B"
    }

    return "${files.size} files · $formattedSize"
}

fun getAppStorageInfo(): Triple<String, String, Double> {
    val folders = listOf(
        "/storage/emulated/0/Music/Cuttify/VideoToAudio",
        "/storage/emulated/0/Movies/Cuttify/Trimmed Video",
        "/storage/emulated/0/Music/Cuttify/MergedAudio",
        "/storage/emulated/0/Music/Cuttify/Trimmed Audio"
    )

    // Total size of all app folders
    val totalUsedBytes = folders.sumOf { path ->
        val folder = File(path)
        if (folder.exists())
            folder.walkBottomUp().filter { it.isFile }.sumOf { it.length() }
        else 0L
    }

    // Available space on device storage
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    val totalBytes = stat.totalBytes

    val percent = if (totalBytes > 0)
        ((totalUsedBytes.toDouble() / totalBytes) * 100 * 10).toLong() / 10.0
    else 0.0

    val usedFormatted = when {
        totalUsedBytes >= 1_073_741_824L -> "%.1f GB".format(totalUsedBytes / 1_073_741_824.0)
        totalUsedBytes >= 1_048_576L     -> "%.1f MB".format(totalUsedBytes / 1_048_576.0)
        totalUsedBytes >= 1_024L         -> "%.1f KB".format(totalUsedBytes / 1_024.0)
        else                             -> "$totalUsedBytes B"
    }

    val totalFormatted = "%.1f GB".format(totalBytes / 1_073_741_824.0)

    return Triple(usedFormatted, totalFormatted, percent)
}


fun searchFiles(query: String): List<Triple<String, String, String>> {
    val folders = listOf(
        "/storage/emulated/0/Music/Cuttify/VideoToAudio",
        "/storage/emulated/0/Movies/Cuttify/Trimmed Video",
        "/storage/emulated/0/Music/Cuttify/MergedAudio",
        "/storage/emulated/0/Music/Cuttify/Trimmed Audio"
    )

    if (query.isBlank()) return emptyList()

    val results = mutableListOf<Triple<String, String, String>>()

    folders.forEach { path ->
        val folder = File(path)
        if (folder.exists()) {
            folder.walkBottomUp()
                .filter { it.isFile }
                .forEach { file ->
                    val sizeFormatted = when {
                        file.length() >= 1_073_741_824L -> "%.1f GB".format(file.length() / 1_073_741_824.0)
                        file.length() >= 1_048_576L     -> "%.1f MB".format(file.length() / 1_048_576.0)
                        file.length() >= 1_024L         -> "%.1f KB".format(file.length() / 1_024.0)
                        else                            -> "${file.length()} B"
                    }
                    // Match by name OR size
                    if (file.name.contains(query, ignoreCase = true) ||
                        sizeFormatted.contains(query, ignoreCase = true)
                    ) {
                        results.add(Triple(file.name, sizeFormatted, file.absolutePath))
                    }
                }
        }
    }

    return results
}