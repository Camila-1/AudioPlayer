package com.example.audioplayer

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel

class AudioPlayerViewModel(private val mediaController: MediaControllerCompat) : ViewModel() {

    fun registerMediaControllerCallback(callback: MediaControllerCompat.Callback) {
        mediaController.registerCallback(callback)
    }

    fun playOrPause() {
        if (mediaController.playbackState?.state == PlaybackStateCompat.STATE_PLAYING)
            mediaController.transportControls?.pause()
        else mediaController.transportControls?.play()
    }

    fun rewind() = mediaController.transportControls?.rewind()

    fun forward() = mediaController.transportControls?.fastForward()
}