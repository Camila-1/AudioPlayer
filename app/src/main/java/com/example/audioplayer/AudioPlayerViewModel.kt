package com.example.audioplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

sealed class MediaPlayerAction {
    object Prepare : MediaPlayerAction()
    object Play : MediaPlayerAction()
    object Pause : MediaPlayerAction()
    class Rewind(val seconds: Int) : MediaPlayerAction()
    class Forward(val seconds: Int) : MediaPlayerAction()
}

class AudioPlayerViewModel : ViewModel() {

    private var isPlaying = false

    val mediaPlayerAction: MutableLiveData<MediaPlayerAction> = MutableLiveData(MediaPlayerAction.Prepare)

    fun playOrPause() {
        if (isPlaying) {
            mediaPlayerAction.value = MediaPlayerAction.Pause
            isPlaying = false
        } else {
            mediaPlayerAction.value = MediaPlayerAction.Play
            isPlaying = true
        }
    }

    fun rewind() {
        mediaPlayerAction.value = MediaPlayerAction.Rewind(15)
    }

    fun forward() {
        mediaPlayerAction.value = MediaPlayerAction.Forward(15)
    }
}