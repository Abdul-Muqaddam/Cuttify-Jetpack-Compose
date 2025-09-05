package com.example.videotoaudioconverter.presentation.audio_player_screen.component

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import android.net.Uri

data class AudioFile(
    val title: String,
    val path: String,
    val artist: String,
    val duration: Long
)

fun getAllAudioFiles(context: Context): List<AudioFile> {
    val audioList = mutableListOf<AudioFile>()

    val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DATA, // Path to file
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION
    )

    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    val cursor = context.contentResolver.query(
        uri,
        projection,
        null,
        null,
        sortOrder
    )

    cursor?.use {
        val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        while (it.moveToNext()) {
            val title = it.getString(titleIndex)
            val path = it.getString(dataIndex)
            val artist = it.getString(artistIndex)
            val duration = it.getLong(durationIndex)

            audioList.add(AudioFile(title, path, artist, duration))
        }
    }

    return audioList
}
