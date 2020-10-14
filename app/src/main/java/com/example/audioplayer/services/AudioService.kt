package com.example.audioplayer.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.audioplayer.R
import com.example.audioplayer.Track
import com.example.audioplayer.application.AudioPlayerApplication
import java.util.*


class AudioService : Service() {

    inner class AudioServiceBinder : Binder() {
        val instance: AudioService
        get() = this@AudioService
    }

    fun seekBarHandle(track: MutableLiveData<Track>) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                TODO("Not yet implemented")
            }

        }, 1000)
    }

    private var notification: Notification? = null

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        AudioPlayerApplication.appComponent.inject(this)
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val uri = "https://www.dropbox.com/s/gbpkwex67ppmj7v/al_green_love_and_happiness_%28NaitiMP3.ru%29.mp3?dl=1"

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

        notification = notification()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    fun play() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun rewind(seconds: Int) {

    }

    fun forward(seconds: Int) {

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

}