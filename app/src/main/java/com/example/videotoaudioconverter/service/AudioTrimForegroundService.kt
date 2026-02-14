package com.example.videotoaudioconverter.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Transformer
import com.example.videotoaudioconverter.presentation.audio_cutter_screen.AudioTrimCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@UnstableApi
class AudioTrimForegroundService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var transformer: Transformer
    private var currentOutputPath: String? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Created")

        transformer = Transformer.Builder(this)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(
                    composition: androidx.media3.transformer.Composition,
                    result: androidx.media3.transformer.ExportResult
                ) {
                    Log.d(TAG, "Trimming Completed")

                    currentOutputPath?.let {
                        AudioTrimCallback.onTrimSuccess(it)
                    }

                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }

                override fun onError(
                    composition: androidx.media3.transformer.Composition,
                    result: androidx.media3.transformer.ExportResult,
                    exception: androidx.media3.transformer.ExportException
                ) {
                    Log.e(TAG, "Trimming Failed", exception)

                    AudioTrimCallback.onTrimError("Trimming failed")

                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            })
            .build()

        createNotificationChannel()
        startForeground(
            NOTIFICATION_ID,
            getForegroundNotification("Preparing audio trimming...")
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val inputPath = intent?.getStringExtra(EXTRA_INPUT_PATH)
        val outputName = intent?.getStringExtra(EXTRA_OUTPUT_NAME) ?: "trimmed_audio.mp3"
            val startMs = intent?.getLongExtra(EXTRA_START_MS, 0L) ?: 0L
        val endMs = intent?.getLongExtra(EXTRA_END_MS, 0L) ?: 0L

        Log.d(TAG, "Received -> input: $inputPath start: $startMs end: $endMs")

        if (inputPath.isNullOrEmpty()) {
            Log.e(TAG, "Input path is null or empty")
            stopSelf()
            return START_NOT_STICKY
        }

        scope.launch {
            trimAudio(inputPath, outputName, startMs, endMs)
        }

        return START_NOT_STICKY
    }

    private suspend fun trimAudio(
        inputPath: String,
        outputName: String,
        startMs: Long,
        endMs: Long
    ) {
        try {
            val inputFile = File(inputPath)
            if (!inputFile.exists()) {
                Log.e(TAG, "Input file does not exist: $inputPath")
                stopSelf()
                return
            }

//            val musicDir =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
//
//            if (!musicDir.exists()) {
//                musicDir.mkdirs()
//            }
//
//            val outputFile = File(musicDir, outputName)

            val trimmedDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                "Cuttify/Trimmed Audio"
            )

            if(!trimmedDir.exists()){
                trimmedDir.mkdirs()
            }

            val outputFile = File(trimmedDir, outputName)

            if (outputFile.exists()) {
                outputFile.delete()
            }

            currentOutputPath = outputFile.absolutePath

            val mediaItem: MediaItem = MediaItem.Builder()
                .setUri(Uri.fromFile(inputFile))
                .setClippingConfiguration(
                    MediaItem.ClippingConfiguration.Builder()
                        .setStartPositionMs(startMs)
                        .setEndPositionMs(endMs)
                        .build()
                )
                .build()

            Log.d(TAG, "Starting transformation...")

            // Switch to Main thread to start Transformer
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                transformer.start(mediaItem, outputFile.absolutePath)
            }

            Log.d(TAG, "Transformation started successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Trimming failed", e)
            stopSelf()
        }
    }

    override fun onDestroy() {
        try {
            transformer.cancel()
        } catch (_: Exception) {
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Trim Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun getForegroundNotification(content: String): Notification {
        val builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                Notification.Builder(this, CHANNEL_ID)
            else
                Notification.Builder(this)

        return builder
            .setContentTitle("Audio Trimming")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
    }

    companion object {
        private const val TAG = "AudioTrimService"
        private const val CHANNEL_ID = "audio_trim_channel"
        private const val NOTIFICATION_ID = 101

        const val EXTRA_INPUT_PATH = "extra_input_path"
        const val EXTRA_OUTPUT_NAME = "extra_output_name"
        const val EXTRA_START_MS = "extra_start_ms"
        const val EXTRA_END_MS = "extra_end_ms"
    }
}

//    import android.app.Notification
//    import android.app.NotificationChannel
//    import android.app.NotificationManager
//    import android.app.Service
//    import android.content.Context
//    import android.content.Intent
//    import android.os.Build
//    import android.os.IBinder
//    import android.util.Log
//    import androidx.media3.common.MediaItem
//    import androidx.media3.common.util.UnstableApi
//    import androidx.media3.transformer.Transformer
//    import androidx.media3.transformer.TransformationRequest
//    import kotlinx.coroutines.CoroutineScope
//    import kotlinx.coroutines.Dispatchers
//    import kotlinx.coroutines.launch
//    import java.io.File
//
//    @UnstableApi
//    class AudioTrimForegroundService : Service() {
//
//        private val scope = CoroutineScope(Dispatchers.IO)
//        private lateinit var transformer: Transformer
//
//        override fun onCreate() {
//            super.onCreate()
//
//            // Initialize Media3 Transformer
//            transformer = Transformer.Builder(this)
//                .setTransformationRequest(
//                    TransformationRequest.Builder()
//                        .setFlattenForSlowMotionEnabled(false)
//                        .build()
//                )
//                .build()
//
//            createNotificationChannel()
//            startForeground(1, getForegroundNotification("Preparing to trim audio..."))
//        }
//
//        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//            val inputPath = intent?.getStringExtra(EXTRA_INPUT_PATH)
//            val outputFileName = intent?.getStringExtra(EXTRA_OUTPUT_NAME) ?: "trimmed_audio.mp3"
//            val startMs = intent?.getLongExtra(EXTRA_START_MS, 0L) ?: 0L
//            val endMs = intent?.getLongExtra(EXTRA_END_MS, 0L) ?: 0L
//
//            if (inputPath != null) {
//                scope.launch {
//                    trimAudio(inputPath, outputFileName, startMs, endMs)
//                }
//            } else {
//                stopSelf()
//            }
//
//            return START_NOT_STICKY
//        }
//
//        private fun trimAudio(inputPath: String, outputFileName: String, startMs: Long, endMs: Long) {
//            try {
//                val inputFile = File(inputPath)
//                val outputFile = File(cacheDir, outputFileName)
//
//                val mediaItem = MediaItem.Builder()
//                    .setUri(inputFile.toURI().toString())
//                    .setClippingConfiguration(
//                        MediaItem.ClippingConfiguration.Builder()
//                            .setStartPositionMs(startMs)
//                            .setEndPositionMs(endMs)
//                            .build()
//                    )
//                    .build()
//
//                transformer.startTransformation(
//                    mediaItem,
//                    outputFile.absolutePath,
//                    object : Transformer.Listener {
//                        override fun onTransformationCompleted(mediaItem: MediaItem) {
//                            Log.d("AudioTrimService", "Audio trimmed successfully: ${outputFile.absolutePath}")
//                            stopSelf()
//                        }
//
//                        override fun onTransformationError(mediaItem: MediaItem, exception: Throwable) {
//                            Log.e("AudioTrimService", "Error trimming audio", exception)
//                            stopSelf()
//                        }
//
//                        override fun onTransformationProgress(mediaItem: MediaItem, progress: Float) {
//                            Log.d("AudioTrimService", "Trimming progress: ${(progress * 100).toInt()}%")
//                        }
//                    }
//                )
//            } catch (e: Exception) {
//                Log.e("AudioTrimService", "Exception in trimming", e)
//                stopSelf()
//            }
//        }
//
//        private fun createNotificationChannel() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(
//                    NOTIFICATION_CHANNEL_ID,
//                    "Audio Trim Service",
//                    NotificationManager.IMPORTANCE_LOW
//                )
//                val manager = getSystemService(NotificationManager::class.java)
//                manager.createNotificationChannel(channel)
//            }
//        }
//
//        private fun getForegroundNotification(content: String): Notification {
//            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
//            } else {
//                Notification.Builder(this)
//            }
//            return builder
//                .setContentTitle("Audio Trimming")
//                .setContentText(content)
//                .setSmallIcon(android.R.drawable.ic_media_play)
//                .build()
//        }
//
//        override fun onBind(intent: Intent?): IBinder? = null
//
//        companion object {
//            const val EXTRA_INPUT_PATH = "extra_input_path"
//            const val EXTRA_OUTPUT_NAME = "extra_output_name"
//            const val EXTRA_START_MS = "extra_start_ms"
//            const val EXTRA_END_MS = "extra_end_ms"
//
//            private const val NOTIFICATION_CHANNEL_ID = "audio_trim_channel"
//        }
//    }