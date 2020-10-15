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


class AudioService : Service(), Runnable {

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
        mediaPlayer.prepare()
        val playbackStateBuilder = PlaybackStateCompat.Builder()

        mediaSession.apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    isActive = true
                    setPlaybackState(
                        playbackStateBuilder
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
                        playbackStateBuilder.setState(
                            PlaybackStateCompat.STATE_PAUSED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                            1F
                        ).build()
                    )
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }

                override fun onRewind() {
                    mediaPlayer.seekTo((mediaPlayer.currentPosition - 15000).toLong(), MediaPlayer.SEEK_PREVIOUS_SYNC)
                }

                override fun onFastForward() {
                    mediaPlayer.seekTo((mediaPlayer.currentPosition + 15000).toLong(), MediaPlayer.SEEK_NEXT_SYNC)
                }
            })

            val activityIntent = Intent(applicationContext, MainActivity::class.java)
            setSessionActivity(PendingIntent.getActivity(applicationContext, 0, activityIntent, 0))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    private fun startForeground() {
        startForeground(1, playerNotification.buildNotification())
    }

    override fun onBind(intent: Intent?): IBinder? = AudioServiceBinder()

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        mediaSession.release()
    }

    override fun run() {
        val intent = Intent(MainActivity.INTENT_ACTION)
            .putExtra("currentPosition", mediaPlayer.currentPosition / 1000)
            .putExtra("duration", mediaPlayer.duration / 1000)
        sendBroadcast(intent)
        handler.postDelayed(this, 500)
    }
}