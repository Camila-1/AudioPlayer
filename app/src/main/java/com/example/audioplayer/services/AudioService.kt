package com.example.audioplayer.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.audioplayer.MainActivity
import com.example.audioplayer.application.AudioPlayerApplication
import com.example.audioplayer.services.notifications.PlayerNotification
import javax.inject.Inject
import kotlin.time.minutes


class AudioService : Service(), Runnable, MediaPlayer.OnCompletionListener {

    @Inject lateinit var mediaPlayer: MediaPlayer
    @Inject lateinit var mediaSession: MediaSessionCompat
    @Inject lateinit var handler: Handler
    @Inject lateinit var playerNotification: PlayerNotification

    inner class AudioServiceBinder : Binder() {
        val instance: AudioService
            get() = this@AudioService
    }

    override fun onCreate() {
        AudioPlayerApplication.appComponent.inject(this)
        super.onCreate()

        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.prepare()

        mediaSession.apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    isActive = true
                    setPlaybackState(
                        PlaybackStateCompat.Builder()
                            .setState(
                                PlaybackStateCompat.STATE_PLAYING,
                                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                                1F
                            ).build()
                    )
                    mediaPlayer.start()
                    startForeground()
                    handler.postDelayed(this@AudioService, 500)
                }

                override fun onPause() {
                    mediaPlayer.pause()
                    setPlaybackState(
                        PlaybackStateCompat.Builder().setState(
                            PlaybackStateCompat.STATE_PAUSED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                            1F
                        ).build()
                    )
                    stopForeground(true)
                }

                override fun onRewind() {
                    mediaPlayer.seekTo(mediaPlayer.currentPosition - 15000L, MediaPlayer.SEEK_PREVIOUS_SYNC)
                }

                override fun onFastForward() {
                    mediaPlayer.seekTo(mediaPlayer.currentPosition + 15000L, MediaPlayer.SEEK_NEXT_SYNC)
                }

                override fun onSeekTo(position: Long) {
                    mediaPlayer.seekTo(position.toInt())
                }
            })
            val activityIntent = Intent(applicationContext, MainActivity::class.java)
            setSessionActivity(PendingIntent.getActivity(applicationContext, 0, activityIntent, 0))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun startForeground() {
        startForeground(1, playerNotification.buildNotification())
    }

    override fun onBind(intent: Intent?): IBinder? = AudioServiceBinder()

    override fun run() {
        val intent = Intent(MainActivity.INTENT_ACTION)
            .putExtra("currentPosition", mediaPlayer.currentPosition / 1000)
            .putExtra("duration", mediaPlayer.duration / 1000)
        sendBroadcast(intent)
        handler.postDelayed(this, 500)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        handler.removeCallbacks(this)
        mediaPlayer.release()
        mediaSession.release()
        stopForeground(true)
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.seekTo(0)
        val intent = Intent(MainActivity.INTENT_ACTION)
            .putExtra("isCompleted", true)
        sendBroadcast(intent)
    }
}