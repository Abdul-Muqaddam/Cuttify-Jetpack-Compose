package com.example.videotoaudioconverter.presentation.Utils

import android.content.ContentValues
import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream

object VideoTrimmerUtil {

    fun trimVideo(
        context: Context,
        inputUri: Uri,
        startMs: Long,
        endMs: Long,
        onProgress: (Int) -> Unit,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        Thread {
            try {
                val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                val outputFile = File(outputDir, "trimmed_${System.currentTimeMillis()}.mp4")

                val extractor = MediaExtractor()
                val fd = context.contentResolver.openFileDescriptor(inputUri, "r")
                    ?: throw Exception("Cannot open video file")
                extractor.setDataSource(fd.fileDescriptor)

                val muxer = MediaMuxer(
                    outputFile.absolutePath,
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
                )

                // Select and map tracks
                val trackMap = mutableMapOf<Int, Int>()
                for (i in 0 until extractor.trackCount) {
                    val format = extractor.getTrackFormat(i)
                    val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
                    if (mime.startsWith("video/") || mime.startsWith("audio/")) {
                        extractor.selectTrack(i)
                        trackMap[i] = muxer.addTrack(format)
                    }
                }

                muxer.start()

                val startUs = startMs * 1000L
                val endUs = endMs * 1000L
                val durationUs = endUs - startUs

                extractor.seekTo(startUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)

                val buffer = java.nio.ByteBuffer.allocate(2 * 1024 * 1024)
                val bufferInfo = android.media.MediaCodec.BufferInfo()

                while (true) {
                    bufferInfo.size = extractor.readSampleData(buffer, 0)
                    if (bufferInfo.size < 0) break

                    val sampleTime = extractor.sampleTime
                    if (sampleTime > endUs) break

                    if (sampleTime >= startUs) {
                        bufferInfo.presentationTimeUs = sampleTime - startUs
                        bufferInfo.flags = extractor.sampleFlags
                        val muxerTrack = trackMap[extractor.sampleTrackIndex]
                        if (muxerTrack != null) {
                            muxer.writeSampleData(muxerTrack, buffer, bufferInfo)
                        }
                        val progress = ((sampleTime - startUs) * 100L / durationUs).toInt()
                        onProgress(progress.coerceIn(0, 99))
                    }

                    extractor.advance()
                }

                muxer.stop()
                muxer.release()
                extractor.release()
                fd.close()

                saveToGallery(context, outputFile)
                onProgress(100)
                onSuccess(outputFile)

            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "Trimming failed")
            }
        }.start()
    }

    private fun saveToGallery(context: Context, file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/Cuttify/Trimmed Video")
            }
            val uri = context.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values
            )
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { out ->
                    FileInputStream(file).copyTo(out)
                }
            }
        }
    }
}