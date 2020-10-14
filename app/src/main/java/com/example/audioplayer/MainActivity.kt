package com.example.audioplayer

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.audioplayer.application.AudioPlayerApplication
import com.example.audioplayer.extensions.injectViewModel
import com.example.audioplayer.services.AudioService
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val INTENT_ACTION = "MEDIA_PLAYER_ACTION"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var audioService: AudioService? = null
    private var mediaController: MediaControllerCompat? = null
    private var audioServiceBinder: AudioService.AudioServiceBinder? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!intent?.action.equals(INTENT_ACTION)) return
            intent?.extras?.getInt("currentPosition")?.let {
                current_time.text = TimeUnit.MILLISECONDS.toMinutes(it.toLong()).toString()
                seek_bar.progress = it
            }
            intent?.extras?.getInt("duration")?.let {
                duration.text = TimeUnit.MILLISECONDS.toMinutes(it.toLong()).toString()
                seek_bar.max = it
            }
        }
    }

    private var serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            audioServiceBinder = service as AudioService.AudioServiceBinder

            mediaController = MediaControllerCompat(
                this@MainActivity, audioServiceBinder!!.sessionToken
            )
            mediaController!!.registerCallback(object : MediaControllerCompat.Callback() {
                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                    if (state?.state == PlaybackStateCompat.STATE_PLAYING)
                        play_pause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                    else play_pause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                }
            })
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioServiceBinder = null
            mediaController = null
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
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter().apply { addAction(INTENT_ACTION) }
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onClick(view: View?) {
        when(view) {
            prev_15 -> { viewModel.rewind() }
            next_15 -> { viewModel.forward() }
            play_pause -> {
                if (mediaController?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING)
                mediaController?.transportControls?.pause()
                else mediaController?.transportControls?.play()
            }
            else -> {  }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}