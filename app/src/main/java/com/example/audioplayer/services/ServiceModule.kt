package com.example.audioplayer.services

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.example.audioplayer.services.notifications.PlayerNotification
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule {

    @Provides
    fun provideMediaController(context: Context, mediaSession: MediaSessionCompat): MediaControllerCompat {
        return MediaControllerCompat(context, mediaSession.sessionToken)
    }

    @Provides
    fun provideHandler(): Handler {
        return Handler()
    }

    @Provides
    fun provideMediaPlayer(context: Context): MediaPlayer {
        val uri =
            "https://www.dropbox.com/s/gbpkwex67ppmj7v/al_green_love_and_happiness_%28NaitiMP3.ru%29.mp3?dl=1"
        return MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(context, Uri.parse(uri))
        }
    }

    @Provides
    fun providePlayerNotification(context: Context): PlayerNotification {
        return PlayerNotification(context)
    }

    @Singleton
    @Provides
    fun provideMediaSession(context: Context): MediaSessionCompat {
        return MediaSessionCompat(context, "AudioPlayer")
    }
}
