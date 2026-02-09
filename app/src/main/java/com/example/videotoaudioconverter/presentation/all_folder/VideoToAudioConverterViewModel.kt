package com.example.videotoaudioconverter.presentation.all_folder

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videotoaudioconverter.presentation.all_video_files.components.getFileName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration



data class VideoToAudioConverterViewModelState(
    val sortFilter: String = "new_to_old",
    val allFolders: List<VideoFolder> = emptyList(),
    val filteredFolders: List<VideoFolder> = emptyList(),
    val IdealTopBar: Boolean = true,
    val SearchTopBar: Boolean = false,
    val searchText: String = "",
    val allVideos: List<VideoFiles> = emptyList(),
    val filteredVideos: List<VideoFiles> = emptyList(),
    val fileTitle: String = ""
)

data class VideoFiles(
    val uri: Uri,
    val fileName: String,
    val dateAdded: Long,
    val size: String,
    val duration: Duration,
    val title: String
)

//data class VideoFolder(
//    val name: String,
//    val path: String,
//    val dateAdded: Long = 0L,
//    val size: Long = 0L
//)

class VideoToAudioConverterViewModel : ViewModel() {

    private val _state = MutableStateFlow(VideoToAudioConverterViewModelState())
    val state: StateFlow<VideoToAudioConverterViewModelState> get() = _state

    fun SearchIconClicked() {
        _state.update {
            it.copy(IdealTopBar = false, SearchTopBar = true)
        }
    }

    fun CrossIconClicked() {
        _state.update {
            it.copy(
                IdealTopBar = true,
                SearchTopBar = false,
                searchText = "",
                filteredVideos = it.allVideos,
                filteredFolders = it.allFolders
            )
        }
    }

    fun folderListUpdate(folders: List<VideoFolder>) {
        _state.update {
            it.copy(
                filteredFolders = folders,
                allFolders = folders
            )
        }
    }

    fun onSearchChangeForVideos(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { state ->
                val filtered = if (value.isBlank()) state.allVideos
                else state.allVideos.filter { it.fileName.contains(value, ignoreCase = true) }

                val sorted = when (state.sortFilter) {
                    "new_to_old" -> filtered.sortedByDescending { it.dateAdded }
                    "old_to_new" -> filtered.sortedBy { it.dateAdded }
                    "Small_To_Large" -> filtered.sortedBy { it.size }
                    "Large_To_Small" -> filtered.sortedByDescending { it.size }
                    "Long_to_Short" -> filtered.sortedByDescending { it.duration }
                    "Short_to_Long" -> filtered.sortedBy { it.duration }
                    "A_to_Z" -> filtered.sortedBy { it.title.lowercase() }
                    "Z_to_A" -> filtered.sortedByDescending { it.title.lowercase() }
                    else -> filtered
                }

                state.copy(searchText = value, filteredVideos = sorted)
            }
        }
    }

    fun onSearchChangeForFolder(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                val filtered = if (value.isBlank()) it.allFolders
                else it.allFolders.filter { folder ->
                    folder.name.contains(value, ignoreCase = true) }

                it.copy(searchText = value, filteredFolders = filtered)
            }
        }
    }

    fun onSortFilterChange(context: Context, sortFilter: String) {
        Toast.makeText(context, sortFilter, Toast.LENGTH_SHORT).show()

        viewModelScope.launch(Dispatchers.IO) {
            _state.update { currentState ->

                // Sort videos
                val sortedVideos = when (sortFilter) {
                    "new_to_old" -> currentState.filteredVideos.sortedByDescending { it.dateAdded }
                    "old_to_new" -> currentState.filteredVideos.sortedBy { it.dateAdded }
                    "Small_To_Large" -> currentState.filteredVideos.sortedBy { it.size }
                    "Large_To_Small" -> currentState.filteredVideos.sortedByDescending { it.size }
                    "Long_to_Short" -> currentState.filteredVideos.sortedByDescending { it.duration }
                    "Short_to_Long" -> currentState.filteredVideos.sortedBy { it.duration }
                    "A_to_Z" -> currentState.filteredVideos.sortedBy { it.title.lowercase() }
                    "Z_to_A" -> currentState.filteredVideos.sortedByDescending { it.title.lowercase() }
                    else -> currentState.filteredVideos
                }

                // Sort folders (same logic)
                val sortedFolders = when (sortFilter) {
                    "new_to_old" -> currentState.filteredFolders.sortedByDescending { it.dateAdded }
                    "old_to_new" -> currentState.filteredFolders.sortedBy { it.dateAdded }
                    "Small_To_Large" -> currentState.filteredFolders.sortedBy { it.size }
                    "Large_To_Small" -> currentState.filteredFolders.sortedByDescending { it.size }
                    "A_to_Z" -> currentState.filteredFolders.sortedBy { it.name.lowercase() }
                    "Z_to_A" -> currentState.filteredFolders.sortedByDescending { it.name.lowercase() }
                    else -> currentState.filteredFolders
                }

                currentState.copy(sortFilter = sortFilter, filteredVideos = sortedVideos, filteredFolders = sortedFolders)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveAllVideos(context: Context, videos: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            val videosFile = videos.map { uri ->
                val projection = arrayOf(
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.TITLE
                )

                val videoData = context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    val dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                    val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                    val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)

                    if (cursor.moveToFirst()) {
                        val dateAdded = cursor.getLong(dateAddedIndex)
                        val size = formatFileSize(cursor.getLong(sizeIndex))
                        val duration = Duration.ofMillis(cursor.getLong(durationIndex))
                        val title = cursor.getString(titleIndex)

                        VideoFiles(uri, getFileName(context, uri), dateAdded, size, duration, title)
                    } else null
                }

                videoData ?: VideoFiles(uri, getFileName(context, uri), 0L, "0 KB", Duration.ZERO, "")
            }

            _state.update { it.copy(allVideos = videosFile, filteredVideos = videosFile) }
        }
    }

    private fun formatFileSize(sizeBytes: Long): String {
        val kb = sizeBytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1) String.format("%.2f MB", mb) else String.format("%.2f KB", kb)
    }
}


//
//data class VideoToAudioConverterViewModelState(
//    val sortFilter: String = "new_to_old",
//    val allFolders: List<VideoFolder> = emptyList(),
//    val filteredFolders: List<VideoFolder> = emptyList(),
//    val IdealTopBar: Boolean = true,
//    val SearchTopBar: Boolean = false,
//    val searchText: String = "",
//    val allVideos: List<VideoFiles> = emptyList(),
//    val filteredVideos: List<VideoFiles> = emptyList(),
//    val fileTitle: String = ""
//)
//
//data class VideoFiles(
//    val uri: Uri,
//    val fileName: String,
//    val dateAdded: Long,
//    val size: String,
//    val duration: Duration,
//    val title: String
//)
//
//
//class VideoToAudioConverterViewModel : ViewModel() {
//    private val _state = MutableStateFlow(VideoToAudioConverterViewModelState())
//    val state: StateFlow<VideoToAudioConverterViewModelState> get() = _state
//
//    fun SearchIconClicked() {
//        _state.update {
//            it.copy(
//                IdealTopBar = false,
//                SearchTopBar = true
//            )
//        }
//    }
//
//    fun folderListUpdate(folders: List<VideoFolder>) {
//        _state.update {
//            it.copy(
//                filteredFolders = folders,
//                allFolders = folders
//            )
//        }
//    }
//
//    fun CrossIconClicked() {
//        _state.update {
//            it.copy(
//                IdealTopBar = true,
//                SearchTopBar = false,
//                searchText = "",
//                filteredVideos = it.allVideos,
//                filteredFolders = it.allFolders
//            )
//        }
//    }
//
//
//    fun onSearchChangeForVideos(value: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            _state.update { state ->
//
//                val filtered = if (value.isBlank()) {
//                    state.allVideos
//                } else {
//                    state.allVideos.filter { video ->
//                        video.fileName.contains(value, ignoreCase = true)
//                    }
//                }
//
//                val sorted = when (state.sortFilter) {
//                    "new_to_old" -> filtered.sortedByDescending { video -> video.dateAdded }
//                    "old_to_new" -> filtered.sortedBy { video -> video.dateAdded }
//
//                    "Small_To_Large" -> filtered.sortedBy { video -> video.size }
//                    "Large_To_Small" -> filtered.sortedByDescending { video -> video.size }
//
//                    "Long_to_Short" -> filtered.sortedByDescending { video -> video.duration }
//                    "Short_to_Long" -> filtered.sortedBy { video -> video.duration }
//
//                    "A_to_Z" -> filtered.sortedBy { video -> video.title.lowercase() }
//                    "Z_to_A" -> filtered.sortedByDescending { video -> video.title.lowercase() }
//
//                    else -> filtered
//                }
//
//                state.copy(
//                    searchText = value,
//                    filteredVideos = sorted
//                )
//            }
//        }
//    }
//    fun onSortFilterChange(context: Context, sortFilter: String) {
//        Toast.makeText(context, sortFilter, Toast.LENGTH_LONG).show()
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _state.update { currentState ->
//
//                // Video sorting (existing)
//                val sortedVideos = when (sortFilter) {
//                    "new_to_old" -> currentState.filteredVideos.sortedByDescending { it.dateAdded }
//                    "old_to_new" -> currentState.filteredVideos.sortedBy { it.dateAdded }
//                    "Small_To_Large" -> currentState.filteredVideos.sortedBy { it.size }
//                    "Large_To_Small" -> currentState.filteredVideos.sortedByDescending { it.size }
//                    "Long_to_Short" -> currentState.filteredVideos.sortedByDescending { it.duration }
//                    "Short_to_Long" -> currentState.filteredVideos.sortedBy { it.duration }
//                    "A_to_Z" -> currentState.filteredVideos.sortedBy { it.title.lowercase() }
//                    "Z_to_A" -> currentState.filteredVideos.sortedByDescending { it.title.lowercase() }
//                    else -> currentState.filteredVideos
//                }
//
//                // Folder sorting
//                val sortedFolders = when (sortFilter) {
//                    "A_to_Z" -> currentState.filteredFolders.sortedBy { it.name.lowercase() }
//                    "Z_to_A" -> currentState.filteredFolders.sortedByDescending { it.name.lowercase() }
//                    else -> currentState.filteredFolders
//                }
//
//                currentState.copy(
//                    sortFilter = sortFilter,
//                    filteredVideos = sortedVideos,
//                    filteredFolders = sortedFolders
//                )
//            }
//        }
//    }
//
//    fun onSearchChangeForFolder(value: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//
//            _state.update {
//
//                val filtered = if (value.isBlank()) {
//                    it.allFolders
//                } else {
//                    it.allFolders.filter { uri ->
//                        uri.name.contains(value, ignoreCase = true)
//                        //                        getFileName(context, uri).contains(value, ignoreCase = true)
//                    }
//                }
//
//                it.copy(
//                    searchText = value,
//                    filteredFolders = filtered,
//
//
//                    )
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun saveAllVideos(context: Context, videos: List<Uri>) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val videosFile = videos.map { uri ->
//
//                val projection = arrayOf(
//                    MediaStore.Video.Media.DATE_ADDED,
//                    MediaStore.Video.Media.SIZE,
//                    MediaStore.Video.Media.DURATION,
//                    MediaStore.Video.Media.TITLE
//                )
//
//                val videoData = context.contentResolver.query(uri, projection, null, null, null)
//                    ?.use { cursor ->
//                        val dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
//                        val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
//                        val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
//                        val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
//
//                        if (cursor.moveToFirst()) {
//                            val dateAdded = cursor.getLong(dateAddedIndex)
//                            val size = formatFileSize(cursor.getLong(sizeIndex))
//                            val duration = Duration.ofMillis(cursor.getLong(durationIndex))
//                            val title = cursor.getString(titleIndex)
//
//                            VideoFiles(
//                                uri = uri,
//                                fileName = getFileName(context, uri),
//                                dateAdded = dateAdded,
//                                size = size,
//                                duration = duration,
//                                title = title
//                            )
//                        } else null
//                    }
//
//                videoData ?: VideoFiles(
//                    uri = uri,
//                    fileName = getFileName(context, uri),
//                    dateAdded = 0L,
//                    size = "0 KB",
//                    duration = Duration.ZERO,
//                    title = ""
//                )
//            }
//
//            _state.update {
//                it.copy(
//                    allVideos = videosFile,
//                    filteredVideos = videosFile
//                )
//            }
//        }
//    }
//
//    private fun formatFileSize(sizeBytes: Long): String {
//        val kb = sizeBytes / 1024.0
//        val mb = kb / 1024.0
//        return if (mb >= 1) {
//            String.format("%.2f MB", mb)
//        } else {
//            String.format("%.2f KB", kb)
//        }
//    }
//
//}