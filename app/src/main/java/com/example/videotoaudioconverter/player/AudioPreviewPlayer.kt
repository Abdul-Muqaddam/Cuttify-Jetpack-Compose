package com.example.videotoaudioconverter.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.videotoaudioconverter.player.AudioController.pause

class AudioPreviewPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var endMs: Long = Long.MAX_VALUE
    private var isPrepared = false

    private val updateRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying && mp.currentPosition >= endMs) {
                    mp.pause()
                } else {
                    handler.postDelayed(this, 50)
                }
            }
        }
    }

    fun load(audioPath: String) {
        release()
        isPrepared = false

        val uri = when {
            audioPath.startsWith("content://") -> Uri.parse(audioPath)
            audioPath.startsWith("file://") -> Uri.parse(audioPath)
            else -> Uri.fromFile(java.io.File(audioPath))
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, uri)
            setOnPreparedListener {
                isPrepared = true
            }
            prepareAsync()
        }
    }

    fun play(startMs: Long, endMs: Long) {
        if (!isPrepared) return
        this.endMs = endMs

        mediaPlayer?.let { mp ->
            mp.seekTo(startMs.toInt())
            mp.start()
            handler.post(updateRunnable)
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        handler.removeCallbacks(updateRunnable)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun seekTo(positionMs: Long) {
        if (isPrepared) mediaPlayer?.seekTo(positionMs.toInt())
    }

    fun currentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    fun release() {
        handler.removeCallbacks(updateRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }
}
