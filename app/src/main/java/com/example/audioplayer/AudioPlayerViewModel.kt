package com.example.audioplayer

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel

class AudioPlayerViewModel(private val mediaController: MediaControllerCompat) : ViewModel() {

    var playbackState: Int = 0

    fun registerMediaControllerCallback(callback: MediaControllerCompat.Callback) {
        mediaController.registerCallback(callback)
    }

    fun playOrPause() {
        if (playbackState == PlaybackStateCompat.STATE_PLAYING)
            mediaController.transportControls?.pause()
        else mediaController.transportControls?.play()
    }

    fun rewind() = mediaController.transportControls?.rewind()

    fun forward() = mediaController.transportControls?.fastForward()

    fun currentPlayPauseImageResource(): Int {
        return if (playbackState == PlaybackStateCompat.STATE_PLAYING)
            R.drawable.ic_baseline_pause_circle_filled_24
        else R.drawable.ic_baseline_play_circle_filled_24
    }
}
