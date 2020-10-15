package com.example.audioplayer

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel

class AudioPlayerViewModel(private val mediaController: MediaControllerCompat) : ViewModel() {

    var playbackState: PlaybackStateCompat? = null

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

    fun playPauseImageResourceByPlaybackState(state: PlaybackStateCompat?): Int {
        playbackState = state
        return if (state?.state == PlaybackStateCompat.STATE_PLAYING)
            R.drawable.ic_baseline_pause_circle_filled_24
        else R.drawable.ic_baseline_play_circle_filled_24
    }

    fun seekTo(seconds: Int) {
        mediaController.transportControls.seekTo(seconds.toLong() * 1000)
    }
}