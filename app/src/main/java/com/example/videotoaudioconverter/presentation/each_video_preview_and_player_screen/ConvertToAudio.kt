package com.example.videotoaudioconverter.presentation.each_video_preview_and_player_screen

import android.content.Context
import android.content.Intent
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.videotoaudioconverter.service.VideoToAudioService
import java.io.File
import java.nio.ByteBuffer


fun extractAudioFromVideo(
    context: Context,
    videoUri: Uri,
    outputFile: File,
    onProgress: (Int) -> Unit,
    onComplete: (Boolean, Exception?) -> Unit
) {
    Thread {
        try {
            val extractor = MediaExtractor()
            extractor.setDataSource(context, videoUri, null)

            val trackCount = extractor.trackCount
            var audioTrackIndex = -1
            var format: MediaFormat? = null

            for (i in 0 until trackCount) {
                format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("audio/") == true) {
                    audioTrackIndex = i
                    break
                }
            }

            if (audioTrackIndex == -1 || format == null) {
                onComplete(false, Exception("No audio track found in the video"))
                return@Thread
            }

            extractor.selectTrack(audioTrackIndex)

            val durationUs = format.getLong(MediaFormat.KEY_DURATION)

            val muxer =
                MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val muxerAudioTrackIndex = muxer.addTrack(format)
            muxer.start()

            val bufferSize = 1 * 1024 * 1024
            val buffer = ByteBuffer.allocate(bufferSize)
            val bufferInfo = MediaCodec.BufferInfo()

            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break

                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = extractor.sampleFlags

                muxer.writeSampleData(muxerAudioTrackIndex, buffer, bufferInfo)
                extractor.advance()

                val progress = ((bufferInfo.presentationTimeUs.toDouble() / durationUs) * 100).toInt()
                    .coerceIn(0, 100)

                onProgress(progress)
            }

            muxer.stop()
            muxer.release()
            extractor.release()

            onComplete(true,null)
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(false,e)
        }
    }.start()
}

fun extractAudioAndSave(
    context: Context,
    videoUri: Uri,
    fileName: String,
    onProgress: (Int) -> Unit,
    onComplete: (File) -> Unit,
    onFailed: () -> Unit
) {

    val audioDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "Cuttify/VideoToAudio"
    )
    if (!audioDir.exists()) audioDir.mkdirs()
    val outputFile = File(audioDir, "$fileName.mp3")




    extractAudioFromVideo(context, videoUri, outputFile, onProgress) { success, exception ->
        if (success) {
            onComplete(outputFile)
        } else {
            Log.d("error", "$exception")
            onFailed()
        }
    }
//    extractAudioFromVideo(context, videoUri, outputFile,
//        onProgress = { progress ->
//            // Forward progress to the service (or notification)
//            onProgress(progress)
//        },
//        onComplete = { success, exception ->
//            if (success) {
//                onComplete(outputFile)
//            } else {
//                Log.d("error", "$exception")
//                onFailed()
//            }
//        }
//    )
//        ) { success,exception ->
//        if (success) {
//            onComplete(outputFile)
//        } else {
//            Log.d("error","$exception")
//            onFailed()
//        }
//    }
}
