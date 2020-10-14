package com.example.audioplayer.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.audioplayer.MainActivity
import com.example.audioplayer.R
import com.example.audioplayer.application.AudioPlayerApplication
import java.util.*


class AudioService : Service(), Runnable {

    inner class AudioServiceBinder : Binder() {
        val instance: AudioService
            get() = this@AudioService
        val sessionToken: MediaSessionCompat.Token
        get() = mediaSession.sessionToken
    }

    private var notification: Notification? = null
    private lateinit var mediaPlayer: MediaPlayer
    private val playbackStateBuilder = PlaybackStateCompat.Builder()
    private lateinit var mediaSession: MediaSessionCompat
    private val handler = Handler()

    override fun onCreate() {
        AudioPlayerApplication.appComponent.inject(this)
        super.onCreate()

        mediaSession = MediaSessionCompat(this, "AudioService").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                @RequiresApi(Build.VERSION_CODES.O)
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
                    handler.postDelayed(this@AudioService, 1000)
                }

                @RequiresApi(Build.VERSION_CODES.N)
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
                    super.onRewind()
                }

                override fun onFastForward() {
                    super.onFastForward()
                }
            })

            val activityIntent = Intent(applicationContext, MainActivity::class.java)
            setSessionActivity(PendingIntent.getActivity(applicationContext, 0, activityIntent, 0))
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val uri =
            "https://www.dropbox.com/s/gbpkwex67ppmj7v/al_green_love_and_happiness_%28NaitiMP3.ru%29.mp3?dl=1"

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, Uri.parse(uri))
            prepare()
        }
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForeground() {
        notification = notification()
        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notification(): Notification? {
        val chan = NotificationChannel(
            "MyChannelId",
            "My Foreground Service",
            NotificationManager.IMPORTANCE_LOW
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_SECRET

        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        val builder = NotificationCompat.Builder(this, "Music")
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_play_circle_filled_24)
            .setContentTitle("App is running on foreground")
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setChannelId("MyChannelId")

        return builder.build()
    }

    override fun onBind(intent: Intent?): IBinder? = AudioServiceBinder()

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        mediaSession.release()
    }

    override fun run() {
        val intent = Intent(MainActivity.INTENT_ACTION)
            .putExtra("currentPosition", mediaPlayer.currentPosition)
            .putExtra("duration", mediaPlayer.duration)
        sendBroadcast(intent)
        handler.postDelayed(this, 1000)
    }
}