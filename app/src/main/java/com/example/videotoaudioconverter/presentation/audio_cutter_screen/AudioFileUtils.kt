package com.example.videotoaudioconverter.presentation.audio_cutter_screen

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object AudioFileUtils {

    fun copyUriToCache(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open input stream")

        val outFile = File(
            context.cacheDir,
            "input_audio_${System.currentTimeMillis()}.mp3"
        )

        FileOutputStream(outFile).use { output ->
            inputStream.copyTo(output)
        }

        return outFile
    }
}



//import android.content.Context
//import android.net.Uri
//import java.io.File
//import java.io.FileOutputStream
//
//object AudioFileUtil {
//
//    fun copyUriToCache(context: Context, uri: Uri): File {
//        val inputStream = context.contentResolver.openInputStream(uri)
//            ?: throw IllegalStateException("Cannot open input stream")
//
//        val file = File(
//            context.cacheDir,
//            "audio_${System.currentTimeMillis()}.mp3"
//        )
//
//        FileOutputStream(file).use { output ->
//            inputStream.copyTo(output)
//        }
//
//        inputStream.close()
//        return file
//    }
//}