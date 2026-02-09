package com.example.videotoaudioconverter.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
//import androidx.privacysandbox.tools.core.generator.build
import com.example.videotoaudioconverter.service.AudioPlayerService
import java.util.concurrent.Executors

object AudioController {

    private var mediaController: MediaController? = null
    private val executor = Executors.newSingleThreadExecutor()

    @OptIn(UnstableApi::class)
    private fun getController(
        context: Context,
        onReady: (MediaController) -> Unit
    ) {
        if (mediaController != null) {
            onReady(mediaController!!)
            return
        }

        val sessionToken = SessionToken(
            context,
            ComponentName(context, AudioPlayerService::class.java)
        )

        val controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            onReady(mediaController!!)
        }, executor)
    }

    @OptIn(UnstableApi::class)
    fun play(context: Context, uri: String, title: String = "Unknown", artist: String = "Cuttify") {
        // Start the service with metadata
        val serviceIntent = Intent(context, AudioPlayerService::class.java).apply {
            putExtra("AUDIO_URI", uri)
            putExtra("TITLE", title)
            putExtra("ARTIST", artist)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }


//    @UnstableApi
//    fun play(
//        context: Context,
//        uri: String,
//        title: String = "Unknown",
//        artist: String = "Cuttify"
//    ) {
//        ensureServiceStarted(context)
//
//        getController(context) { controller ->
//            val mediaItem = MediaItem.Builder()
//                .setUri(uri)
//                .setMediaMetadata(
//                    MediaMetadata.Builder()
//                        .setTitle(title)
//                        .setArtist(artist)
//                        .build()
//                )
//                .build()
//
//            controller.setMediaItem(mediaItem)
//            controller.prepare()
//            controller.play()
//        }
//    }

    fun pause() {
        mediaController?.pause()
    }

    fun resume() {
        mediaController?.play()
    }

    fun next() {
        mediaController?.seekToNext()
    }

    fun previous() {
        mediaController?.seekToPrevious()
    }

    fun stop() {
        mediaController?.stop()
    }

    @UnstableApi
    fun ensureServiceStarted(context: Context) {
        val intent = Intent(context, AudioPlayerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}


//object AudioController {
//
//    private var mediaController: MediaController? = null
//    private val executor = Executors.newSingleThreadExecutor()
//
//    /* -------------------- GET OR CREATE CONTROLLER -------------------- */
//    @OptIn(UnstableApi::class)
//    private fun getController(
//        context: Context,
//        onReady: (MediaController) -> Unit
//    ) {
//        if (mediaController != null) {
//            onReady(mediaController!!)
//            return
//        }
//
//        val sessionToken = SessionToken(
//            context,
//            ComponentName(context, AudioPlayerService::class.java)
//        )
//
//        val controllerFuture =
//            MediaController.Builder(context, sessionToken).buildAsync()
//
//        controllerFuture.addListener({
//            mediaController = controllerFuture.get()
//            onReady(mediaController!!)
//        }, executor)
//    }
//
//    /* -------------------- PLAY SINGLE AUDIO -------------------- */
//    fun play(
//        context: Context,
//        uri: String,
//        title: String = "Unknown",
//        artist: String = "Cuttify"
//    ) {
//        getController(context) { controller ->
//            val mediaItem = MediaItem.Builder()
//                .setUri(uri)
//                .setMediaMetadata(
//                    MediaMetadata.Builder()
//                        .setTitle(title)
//                        .setArtist(artist)
//                        .build()
//                )
//                .build()
//
//            controller.setMediaItem(mediaItem)
//            controller.prepare()
//            controller.play()
//        }
//    }
//
//    /* -------------------- PLAY PLAYLIST -------------------- */
//    fun playPlaylist(
//        context: Context,
//        items: List<Pair<String, String>>, // Pair<uri, title>
//        startIndex: Int = 0
//    ) {
//        getController(context) { controller ->
//
//            val mediaItems = items.map { (uri, title) ->
//                MediaItem.Builder()
//                    .setUri(uri)
//                    .setMediaMetadata(
//                        MediaMetadata.Builder()
//                            .setTitle(title)
//                            .setArtist("Cuttify")
//                            .build()
//                    )
//                    .build()
//            }
//
//            controller.setMediaItems(mediaItems, startIndex, 0L)
//            controller.prepare()
//            controller.play()
//        }
//    }
//
//    /* -------------------- CONTROLS -------------------- */
//    fun pause() {
//        mediaController?.pause()
//    }
//
//    fun resume() {
//        mediaController?.play()
//    }
//
//    fun stop() {
//        mediaController?.stop()
//    }
//
//    fun next() {
//        mediaController?.seekToNext()
//    }
//
//    fun previous() {
//        mediaController?.seekToPrevious()
//    }
//
//    @OptIn(UnstableApi::class)
//    fun ensureServiceStarted(context: Context) {
//        val intent = Intent(context, AudioPlayerService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent)
//        } else {
//            context.startService(intent)
//        }
//    }
////    fun ensureServiceStarted(context: Context) {
////        val intent = android.content.Intent(context, AudioPlayerService::class.java)
////        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////            context.startForegroundService(intent)
////        } else {
////            context.startService(intent)
////        }
////    }
//}
