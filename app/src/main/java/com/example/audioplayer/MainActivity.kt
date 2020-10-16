package com.example.audioplayer

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audioplayer.application.AudioPlayerApplication
import com.example.audioplayer.extensions.injectViewModel
import com.example.audioplayer.network.InternetChecker
import com.example.audioplayer.services.AudioService
import com.example.audioplayer.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val INTENT_ACTION = "MEDIA_PLAYER_ACTION"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: AudioPlayerViewModel

    private var audioServiceBinder: AudioService.AudioServiceBinder? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!intent?.action.equals(INTENT_ACTION)) return
            intent?.extras?.getInt("currentPosition")?.let {
                current_time.text = Utils.secondsToMS(it.toLong())
                seek_bar.progress = it
            }
            intent?.extras?.getInt("duration")?.let {
                duration.text = Utils.secondsToMS(it.toLong())
                seek_bar.max = it
            }
            intent?.extras?.getBoolean("isCompleted").let {
                if (it == true) {
                    seek_bar.progress = 0
                    viewModel.playbackState = PlaybackStateCompat.STATE_PAUSED
                    play_pause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                }
            }
            intent?.extras?.getBoolean("isNotConnected").let {
                if (it == true)
                    Toast.makeText(this@MainActivity, "No connection", Toast.LENGTH_LONG).show()
            }
        }
    }

    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            audioServiceBinder = service as AudioService.AudioServiceBinder

            val mediaControllerCallback = object : MediaControllerCompat.Callback() {
                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                    state?.let {
                        viewModel.playbackState = it.state
                        val resId = viewModel.currentPlayPauseImageResource()
                        setPlayPauseButtonImageResource(resId)
                    }
                }
            }
            viewModel.registerMediaControllerCallback(mediaControllerCallback)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioServiceBinder = null
            mediaController = null
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        AudioPlayerApplication.appComponent.inject(this)

        viewModel = injectViewModel(viewModelFactory)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        play_pause.setOnClickListener(this)
        prev_15.setOnClickListener(this)
        next_15.setOnClickListener(this)
        seek_bar.setOnTouchListener { _, _ -> true }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter().apply { addAction(INTENT_ACTION) }
        registerReceiver(receiver, intentFilter)
        Intent(this, AudioService::class.java).also { intent ->
            startService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        val resId = viewModel.currentPlayPauseImageResource()
        setPlayPauseButtonImageResource(resId)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onClick(view: View?) {
        when(view) {
            prev_15 -> {
                viewModel.rewind()
            }
            next_15 -> {
                viewModel.forward()
            }
            play_pause -> {
                viewModel.playOrPause()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    private fun setPlayPauseButtonImageResource(resId: Int) = play_pause.setImageResource(resId)
}