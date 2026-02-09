package com.example.videotoaudioconverter.audio

import android.os.Environment
import com.arthenica.ffmpegkit.FFmpegKit
import java.io.File
import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.nio.ByteBuffer

object AudioExporter {

    fun trimAudio(
        inputPath: String,
        startMs: Long,
        endMs: Long,
        outputFile: File
    ) {
        val extractor = MediaExtractor()
        extractor.setDataSource(inputPath)

        val trackIndex = selectAudioTrack(extractor)
        if (trackIndex < 0) throw IllegalStateException("No audio track found")

        extractor.selectTrack(trackIndex)
        val format = extractor.getTrackFormat(trackIndex)
        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        val muxerTrackIndex = muxer.addTrack(format)
        muxer.start()

        val bufferSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        val buffer = ByteBuffer.allocate(bufferSize)
        val bufferInfo = android.media.MediaCodec.BufferInfo()

        extractor.seekTo(startMs * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

        while (true) {
            val sampleTime = extractor.sampleTime
            if (sampleTime < 0 || sampleTime > endMs * 1000) break

            bufferInfo.offset = 0
            bufferInfo.size = extractor.readSampleData(buffer, 0)
            bufferInfo.presentationTimeUs = sampleTime - startMs * 1000
            bufferInfo.flags = extractor.sampleFlags

            muxer.writeSampleData(muxerTrackIndex, buffer, bufferInfo)
            extractor.advance()
        }

        muxer.stop()
        muxer.release()
        extractor.release()
    }

    private fun selectAudioTrack(extractor: MediaExtractor): Int {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) return i
        }
        return -1
    }
}


//import android.content.Context
//import android.media.*
//import android.os.Environment
//import java.io.File
//import java.nio.ByteBuffer
//
//object AudioExporter {
//
//    fun trimAudio(
//        context: Context,
//        inputPath: String,
//        startMs: Long,
//        endMs: Long,
//        outputFileName: String
//    ): File {
//
//        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!
//        val outputFile = File(outputDir, outputFileName)
//
//        val startSec = startMs / 1000f
//        val durationSec = (endMs - startMs) / 1000f
//
//        val extractor = android.media.MediaExtractor()
//        extractor.setDataSource(inputPath)
//        val muxer = android.media.MediaMuxer(outputFile.absolutePath, android.media.MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
//
//        // Use your previous MediaCodec/BufferInfo logic here to trim audio
//
//        extractor.release()
//        muxer.stop()
//        muxer.release()
//
//        return outputFile
//    }
//}



//import android.content.Context
//import android.os.Environment
//import com.arthenica.ffmpegkit.FFmpegKit
//import com.arthenica.ffmpegkit.ReturnCode
//import java.io.File
//
//object AudioExporter {
//
//    fun trimAudio(
//        context: Context,
//        inputPath: String,
//        startMs: Long,
//        endMs: Long,
//        outputFileName: String
//    ): File {
//
//        val musicDir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
//
//        val outputFile = File(musicDir, outputFileName)
//
//        val startSec = startMs / 1000f
//        val durationSec = (endMs - startMs) / 1000f
//
//        val command = """
//            -y -i "$inputPath"
//            -ss $startSec
//            -t $durationSec
//            -c copy
//            "${outputFile.absolutePath}"
//        """.trimIndent()
//
//        FFmpegKit.execute(command)
//
//        return outputFile
//    }
//}