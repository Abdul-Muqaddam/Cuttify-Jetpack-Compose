package com.example.videotoaudioconverter.presentation.audio_player_screen.component

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.net.Uri
import java.io.File

data class AudioFile(
    val title: String,
    val path: String,
    val artist: String,
    val duration: Int,
    val uri: Uri
)


//fun getAllAudioFiles(context: Context): List<AudioFile> {
//    val audioList = mutableListOf<AudioFile>()
//
//    val projection = arrayOf(
//        MediaStore.Audio.Media._ID,
//        MediaStore.Audio.Media.TITLE,
//        MediaStore.Audio.Media.ARTIST,
//        MediaStore.Audio.Media.DURATION
//    )
//
//    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0" // Only music files
//    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
//
//    val cursor = context.contentResolver.query(
//        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//        projection,
//        selection,
//        null,
//        sortOrder
//    )
//
//    cursor?.use {
//        val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
//        val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
//        val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
//        val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//
//        while (it.moveToNext()) {
//            val id = it.getLong(idIndex)
//            val title = it.getString(titleIndex) ?: "Unknown Title"
//            val artist = it.getString(artistIndex) ?: "Unknown Artist"
//            val duration = it.getLong(durationIndex)
//
//            val contentUri: Uri = ContentUris.withAppendedId(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                id
//            )
//
//            audioList.add(AudioFile(title, artist, duration, contentUri))
//        }
//    }
//
//    return audioList
//}


fun getAllAudioFiles(context: Context): List<AudioFile> {
    val audioList = mutableListOf<AudioFile>()

    val contentResolver = context.contentResolver
    val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    val projection = arrayOf(
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DATA, // Path to file
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media._ID
    )
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

//    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
//
//    val cursor = context.contentResolver.query(
//        uri,
//        projection,
//        null,
//        null,
//        sortOrder
//    )
//
//    cursor?.use {
//        val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
//        val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
//        val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
//        val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//
//        while (it.moveToNext()) {
//            val title = it.getString(titleIndex)
//            val path = it.getString(dataIndex)
//            val artist = it.getString(artistIndex)
//            val duration = it.getLong(durationIndex)
//
//            audioList.add(AudioFile(title, path, artist, duration))
//        }
//    }

    contentResolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val title = cursor.getString(titleColumn) ?: "Unknown"
            val artist = cursor.getString(artistColumn) ?: "Unknown"
//            val path = cursor.getString(pathColumn) ?: ""
            val duration = cursor.getInt(durationColumn)

            val contentUri: Uri = ContentUris.withAppendedId(uri, id)
            val path = contentUri.toString()

            audioList.add(AudioFile(title, path, artist, duration, contentUri))
        }
    }
    return audioList
}
