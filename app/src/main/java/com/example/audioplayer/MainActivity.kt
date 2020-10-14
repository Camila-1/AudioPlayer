package com.example.audioplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.lifecycle.Observer
import com.example.audioplayer.application.AudioPlayerApplication
import com.example.audioplayer.extensions.injectViewModel
import com.example.audioplayer.services.AudioService
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var audioService: AudioService? = null

    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            audioService = (service as AudioService.AudioServiceBinder).instance
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
        }
    }

    lateinit var viewModel: AudioPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AudioPlayerApplication.appComponent.inject(this)

        viewModel = injectViewModel(viewModelFactory)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        play_pause.setOnClickListener(this)
        prev_15.setOnClickListener(this)
        next_15.setOnClickListener(this)

        Intent(this, AudioService::class.java).also { intent ->
            startService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        viewModel.mediaPlayerAction.observe(this, { playerAction ->
            when(playerAction) {
                is MediaPlayerAction.Prepare ->
                    play_pause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                is MediaPlayerAction.Pause -> {
                    audioService?.pause()
                    play_pause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                }
                is MediaPlayerAction.Play -> {
                    audioService?.play()
                    play_pause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                }
                is MediaPlayerAction.Rewind -> audioService?.rewind(playerAction.seconds)
                is MediaPlayerAction.Forward -> audioService?.forward(playerAction.seconds)
            }
        })
    }

    override fun onClick(view: View?) {
        when(view) {
            prev_15 -> { viewModel.rewind() }
            next_15 -> { viewModel.forward() }
            play_pause -> { viewModel.playOrPause() }
            else -> {  }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        audioService?.stopSelf()
    }
}