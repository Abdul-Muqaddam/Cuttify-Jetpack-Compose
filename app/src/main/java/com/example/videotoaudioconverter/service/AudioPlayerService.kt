package com.example.videotoaudioconverter.service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.NotificationUtil.createNotificationChannel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaNotification
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.videotoaudioconverter.MainActivity
import com.example.videotoaudioconverter.R
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture


@UnstableApi
class AudioPlayerService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var playerNotificationManager: PlayerNotificationManager

    companion object {
        const val CHANNEL_ID = "audio_playback_channel"
        const val NOTIFICATION_ID = 101
        const val ACTION_STOP = "ACTION_STOP"

    }

    override fun onCreate() {
        super.onCreate()
       createNotificationChannel()

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this)
            .build()
            .apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .setUsage(C.USAGE_MEDIA)
                        .build(),
                    true
                )
                setHandleAudioBecomingNoisy(true)

                // Add player listener for metadata updates
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_ENDED -> {
                                // When playback ends, stop foreground service but keep notification
                                stopForeground(STOP_FOREGROUND_REMOVE)
                            }
                            Player.STATE_IDLE -> {
                                // When idle (stopped), remove foreground notification
                                stopForeground(STOP_FOREGROUND_REMOVE)
                            }
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (!isPlaying) {
                            // When paused, don't remove the notification entirely
                            // Just update it to show paused state
                            updateNotification()
                        }
                    }
                })
            }

        // Initialize MediaSession
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(object : MediaSession.Callback {
                override fun onAddMediaItems(
                    mediaSession: MediaSession,
                    controllerInfo: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>
                ): ListenableFuture<List<MediaItem>> {
                    return Futures.immediateFuture(mediaItems)
                }
            })
            .build()


        setupNotificationManager()
    }
    private fun setupNotificationManager() {
        val notificationListener = object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                // Only start as foreground when actually playing
                if (ongoing && player.isPlaying) {
                    startForeground(notificationId, notification)
                }
            }

            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                if (dismissedByUser) {
                    // Stop service if user swipes away notification
                    stopSelf()
                }
            }
        }

        // Configure PlayerNotificationManager
        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            NOTIFICATION_ID,
            CHANNEL_ID
        ).apply {
            setNotificationListener(notificationListener)
            setMediaDescriptionAdapter(MediaDescriptionAdapter())
            setSmallIconResourceId(R.drawable.ic_music_fill)
            setChannelNameResourceId(R.string.app_name)
            setChannelDescriptionResourceId(R.string.app_name)
        }.build()

        // Set notification behavior
        playerNotificationManager.setPlayer(player)
        playerNotificationManager.setUseStopAction(true)
        playerNotificationManager.setUsePreviousAction(false)
        playerNotificationManager.setUseNextAction(false)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media controls for Cuttify"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                // IMPORTANT: Prevent multiple notifications
                setSound(null, null)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private inner class MediaDescriptionAdapter :
        PlayerNotificationManager.MediaDescriptionAdapter {

        override fun getCurrentContentTitle(player: Player): CharSequence {
            return player.currentMediaItem?.mediaMetadata?.title?.toString() ?: "Unknown"
        }

        override fun getCurrentContentText(player: Player): CharSequence {
            return player.currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Cuttify"
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): android.graphics.Bitmap? {
            return null
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val intent = Intent(this@AudioPlayerService, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            return PendingIntent.getActivity(
                this@AudioPlayerService,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("AudioService", "Received action: ${intent?.action}")

        when (intent?.action) {
            "PLAY_AUDIO" -> {
                Log.d("AudioService", "Handling PLAY_AUDIO")
                intent.getStringExtra("AUDIO_URI")?.let { uri ->
                    val title = intent.getStringExtra("TITLE") ?: "Unknown"
                    val artist = intent.getStringExtra("ARTIST") ?: "Cuttify"
                    playAudio(uri, title, artist)
                }
            }
            "PAUSE_AUDIO" -> {
                Log.d("AudioService", "Handling PAUSE_AUDIO command")
                if (player.isPlaying) {
                    player.pause()
                    Log.d("AudioService", "Audio paused successfully")
                    // Update notification to show paused state
                    playerNotificationManager.invalidate()
                } else {
                    Log.d("AudioService", "Audio already paused")
                }
            }
            "RESUME_AUDIO" -> {
                Log.d("AudioService", "Handling RESUME_AUDIO command")
                if (!player.isPlaying) {
                    player.play()
                    Log.d("AudioService", "Audio resumed successfully")
                    playerNotificationManager.invalidate()
                }
            }
            ACTION_STOP -> {
                Log.d("AudioService", "Handling STOP command")
                player.stop()
                stopSelf()
                return START_NOT_STICKY
            }
        }

        return START_STICKY
    }

    fun playAudio(uri: String, title: String = "Unknown", artist: String = "Cuttify") {
        try {
            if (uri.isBlank()) {
                return
            }

            // Check if we're already playing this URI
            val currentUri = player.currentMediaItem?.requestMetadata?.mediaUri?.toString()

            if (currentUri == uri && player.isPlaying) {
                // Already playing this audio, do nothing
                Log.d("AudioService", "Already playing this audio")
                return
            }

            if (currentUri == uri && !player.isPlaying) {
                // Same audio but paused, just resume it
                Log.d("AudioService", "Resuming paused audio")
                player.play()
                return
            }

            // New audio or different audio - load and play
            Log.d("AudioService", "Loading new audio: $title")

            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(uri))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(title)
                        .setArtist(artist)
                        .build()
                )
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()

            // Make sure notification shows as foreground
            playerNotificationManager.invalidate()

        } catch (e: Exception) {
            Log.e("AudioService", "Error playing audio", e)
            e.printStackTrace()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        // IMPORTANT: Proper cleanup order
        playerNotificationManager.setPlayer(null)
        player.release()
        mediaSession.release()
        // Stop foreground and remove notification
        stopForeground(true)
        super.onDestroy()
    }

    private fun updateNotification() {
        playerNotificationManager.invalidate()
    }
}