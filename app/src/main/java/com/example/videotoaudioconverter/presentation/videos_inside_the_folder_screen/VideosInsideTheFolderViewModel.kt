package com.example.videotoaudioconverter.presentation.videos_inside_the_folder_screen

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videotoaudioconverter.presentation.all_folder.VideoFiles
import com.example.videotoaudioconverter.presentation.all_video_files.components.getFileName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration

data class VideosInsideTheFolderViewModelState(
    val sortFilter: String = "new_to_old",
    val isShowBottomSheet: Boolean=false,
    val IdealTopBar: Boolean = true,
    val SearchTopBar: Boolean = false,
    val searchText: String = "",
    val filterVideosList: List<VideoFiles> = emptyList()
)

data class VideoFiles(
    val uri: Uri,
    val fileName: String,
    val dateAdded: Long,
    val size: String,
    val duration: Duration,
    val title: String
)

class VideoInsideTheFolderViewModel: ViewModel(){
    private val _state = MutableStateFlow(VideosInsideTheFolderViewModelState())
    val state: StateFlow<VideosInsideTheFolderViewModelState> get() = _state

    fun SearchIconClicked() {
        _state.update {
            it.copy(
                IdealTopBar = false,
                SearchTopBar = true
            )
        }
    }
    fun CrossIconClicked() {
        _state.update {
            it.copy(
                IdealTopBar = true,
                SearchTopBar = false
            )
        }
    }
    fun isShowBottomSheetChange(state: Boolean){
        _state.update {
            it.copy(
                isShowBottomSheet = state
            )
        }
    }

    fun onSortFilterChange(sortFilter: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { currentState ->
                val sorted = when (sortFilter) {
                    "new_to_old" -> currentState.filterVideosList.sortedByDescending { it.dateAdded }
                    "old_to_new" -> currentState.filterVideosList.sortedBy { it.dateAdded }

                    "Small_To_Large" -> currentState.filterVideosList.sortedBy { it.size }
                    "Large_To_Small" -> currentState.filterVideosList.sortedByDescending { it.size }

                    "Long_to_Short" -> currentState.filterVideosList.sortedByDescending { it.duration }
                    "Short_to_Long" -> currentState.filterVideosList.sortedBy { it.duration }

                    "A_to_Z" -> currentState.filterVideosList.sortedBy { it.title.lowercase() }
                    "Z_to_A" -> currentState.filterVideosList.sortedByDescending { it.title.lowercase() }

                    else -> currentState.filterVideosList
                }


                currentState.copy(
                    sortFilter = sortFilter,
                    filterVideosList = sorted,
                )
            }
        }
    }

    fun onSearchChange(value: String) {
        viewModelScope.launch(Dispatchers.IO) {

            _state.update {

                val filtered = if (value.isBlank()) {
                    it.filterVideosList
                } else {
                    it.filterVideosList.filter { uri ->
                        uri.fileName.contains(value, ignoreCase = true)
                    }
                }

                it.copy(
                    searchText = value,
                    filterVideosList = filtered
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun videoFilesUpdate(context: Context, videos: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            val videosFile = videos.map { uri ->

                val projection = arrayOf(
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.TITLE
                )

                val videoData = context.contentResolver.query(uri, projection, null, null, null)
                    ?.use { cursor ->
                        val dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                        val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                        val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                        val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)

                        if (cursor.moveToFirst()) {
                            val dateAdded = cursor.getLong(dateAddedIndex)
                            val size = formatFileSize(cursor.getLong(sizeIndex))
                            val duration = Duration.ofMillis(cursor.getLong(durationIndex))
                            val title = cursor.getString(titleIndex)

                            VideoFiles(
                                uri = uri,
                                fileName = getFileName(context, uri),
                                dateAdded = dateAdded,
                                size = size,
                                duration = duration,
                                title = title
                            )
                        } else null
                    }

                videoData ?: VideoFiles(
                    uri = uri,
                    fileName = getFileName(context, uri),
                    dateAdded = 0L,
                    size = "0 KB",
                    duration = Duration.ZERO,
                    title = ""
                )
            }

            _state.update {
                it.copy(
//                    allVideos = videosFile,
                    filterVideosList = videosFile
                )
            }
        }
    }

    private fun formatFileSize(sizeBytes: Long): String {
        val kb = sizeBytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1) {
            String.format("%.2f MB", mb)
        } else {
            String.format("%.2f KB", kb)
        }
    }

}