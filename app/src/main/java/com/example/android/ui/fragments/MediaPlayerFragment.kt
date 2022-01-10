package com.example.android.ui.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.R
import com.example.android.base.BaseFragment
import com.example.android.base.BaseViewModel
import com.example.android.media.service.MediaPlayerService
import com.example.android.ui.activities.MainActivity
import com.example.android.viewmodels.MediaViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MediaPlayerFragment : BaseFragment(), Player.Listener {
    private val viewModel: MediaViewModel by viewModels()
    private var exoPlayer: ExoPlayer? = null
    private val handler: Handler = Handler()
    //private var _binding: FragmentMediaPlayerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    // private val binding get() = _binding!!

    var isPlaying: Boolean = false
    var mService: MediaPlayerService? = null
    var mBound: Boolean = false
    private var dragging: Boolean = false

    private val args: MediaPlayerFragmentArgs by navArgs()
    lateinit var playCheckbox: AppCompatCheckBox
    lateinit var favouriteCheckBox: AppCompatCheckBox
    lateinit var progressBar: ProgressBar
    lateinit var currentTimeTextView: TextView
    lateinit var totalTimeTextView: TextView
    lateinit var titleTextView: TextView
    lateinit var forwardImageView: ImageView
    lateinit var rewindImageView: ImageView
    lateinit var nextImageView: ImageView
    lateinit var backImageView: ImageView
    lateinit var closeImageView: ImageView
    lateinit var stopImageView: ImageView

    var mediaPlayerFragmentStateListener: MediaPlayerFragmentStateListener? = null

    private val connection = object : ServiceConnection {
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Timber.d("Media Player Fragment onServiceConnected")
            // Because we have bound to an explicit
            // service that is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            val binder = service as MediaPlayerService.LocalBinder
            mService = binder.getService()
            mService?.let { mediaPlayerService ->

                exoPlayer = mediaPlayerService.exoPlayer
                exoPlayer = mService?.exoPlayer

                exoPlayer?.addListener(this@MediaPlayerFragment)

                if (args.surah.id > -1) {

                    mService?.setMediaItem(args.surah.audios)
                    viewModel.isAudioItemExist(args.surah.audios[exoPlayer?.currentMediaItemIndex!!])
                    exoPlayer?.playWhenReady = true
                } else {
                    mService?.setMediaItem(mediaPlayerService.audioItems)
                    viewModel.isAudioItemExist(mediaPlayerService.audioItems[exoPlayer?.currentMediaItemIndex!!])
                    exoPlayer?.playWhenReady = true
                }
            }
            mBound = true
            mediaPlayerFragmentStateListener?.onServiceContected()
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Timber.d("Media Player Fragment onServiceDisconnected")
            exoPlayer?.removeListener(this@MediaPlayerFragment)
            mBound = false
            mediaPlayerFragmentStateListener?.onServiceDisContected()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_media_player, container, false)
        // Inflate the layout for this fragment
        //_binding = FragmentMediaPlayerBinding.inflate(inflater, container, false)
        // val view = binding.root
        playCheckbox = view.findViewById(R.id.play_checkbox)
        progressBar = view.findViewById(R.id.progressBar)
        currentTimeTextView = view.findViewById(R.id.current_time_text_view)
        totalTimeTextView = view.findViewById(R.id.total_time_text_view)
        titleTextView = view.findViewById(R.id.title_textView)
        forwardImageView = view.findViewById(R.id.forward_image_view)
        rewindImageView = view.findViewById(R.id.rewind_image_view)

        nextImageView = view.findViewById(R.id.next_image_view)
        backImageView = view.findViewById(R.id.back_image_view)

        closeImageView = view.findViewById(R.id.close_image_view)
        stopImageView = view.findViewById(R.id.stop_image)
        favouriteCheckBox = view.findViewById(R.id.favourite_checkbox)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            playCheckbox.setOnClickListener {

                exoPlayer?.let { player ->
                    if (player.isPlaying)
                        player.pause()
                    else
                        player.play()
                }
            }

            forwardImageView.setOnClickListener {
                exoPlayer?.seekForward()
            }

            rewindImageView.setOnClickListener {
                exoPlayer?.seekBack()
            }

            closeImageView.setOnClickListener {
                findNavController().popBackStack()
            }
            stopImageView.setOnClickListener {
                exoPlayer?.stop()
                exoPlayer?.release()


                Intent(activity, MediaPlayerService::class.java).also { intent ->
                    activity?.stopService(intent)
                }

                findNavController().popBackStack()
            }

            favouriteCheckBox.setOnClickListener {
                mService?.let { mediaPlayerService ->
                    viewModel.insertFavouriteSurahList(mediaPlayerService.audioItems[exoPlayer?.currentMediaItemIndex!!])
                }

            }

            nextImageView.setOnClickListener {
                exoPlayer?.seekToNextMediaItem()
            }

            backImageView.setOnClickListener {
                exoPlayer?.seekToPreviousMediaItem()
            }


            viewModel.isFavourite.observe(viewLifecycleOwner, Observer {
                favouriteCheckBox.isChecked = it
            })

        }
    }

    override fun setListeners() {

    }

    override fun setViewModel(): BaseViewModel? {
        return viewModel
    }

    override fun onStart() {
        super.onStart()
        activity?.let { fragmentActivity ->

            Intent((activity as MainActivity), MediaPlayerService::class.java).also { intent ->
                activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }

            Util.startForegroundService(
                fragmentActivity,
                Intent(fragmentActivity, MediaPlayerService::class.java)
            )

            // Handler().postDelayed({
//                exoPlayer = mService?.exoPlayer
//
//                exoPlayer?.addListener(this@MediaPlayerFragment)
//                exoPlayer?.playWhenReady = true
//                mService?.setMediaItem(args.surah.audios)
//                viewModel.isAudioItemExist(args.surah.audios[exoPlayer?.currentMediaItemIndex!!])

            // }, 1000)


        }
    }

    override fun onStop() {
        super.onStop()
        activity?.let {
            it.unbindService(connection)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        handler.removeCallbacks(updateProgressAction)
        exoPlayer?.removeListener(this@MediaPlayerFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MediaPlayerFragmentStateListener){
            mediaPlayerFragmentStateListener = context
        }
    }

    private val updateProgressAction = Runnable {
        updateProgressBar()
    }

    private fun updateProgressBar() {

        exoPlayer?.let { player ->

            val duration = player.duration
            val position = player.currentPosition

            setPlayerTiming(position, duration)

            if (!dragging) {
                progressBar.progress = (position).toInt()
            }
            val bufferedPosition = player.bufferedPosition
            progressBar.secondaryProgress = (bufferedPosition).toInt()

            // Remove scheduled updates.
            handler.removeCallbacks(updateProgressAction)

            // Schedule an update if necessary.
            val playbackState = player.playbackState

            if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                var delayMs: Long

                if (player.playWhenReady && playbackState == Player.STATE_READY) {
                    delayMs = 1000 - position % 1000
                    if (delayMs < 200) {
                        delayMs += 1000
                    }
                } else {
                    delayMs = 1000
                }
                handler.postDelayed(updateProgressAction, delayMs)
            }
        }

    }

    private fun setPlayerTiming(position: Long, duration: Long) {
        val currentTime = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(position) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    position
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(position) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    position
                )
            )
        )


        currentTimeTextView.text = currentTime

        val totalTime = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    duration
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    duration
                )
            )
        )

        totalTimeTextView.text = totalTime
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        when (playbackState) {
            Player.STATE_BUFFERING -> {
                isPlaying = false
                playCheckbox.isChecked = false

            }
            Player.STATE_ENDED -> {
                isPlaying = false
                playCheckbox.isChecked = false
            }
            Player.STATE_IDLE -> {
                isPlaying = false
                playCheckbox.isChecked = false
            }
            Player.STATE_READY -> {
                isPlaying = true
                progressBar.max = exoPlayer?.duration?.toInt() ?: 0
                updateProgressBar()
                playCheckbox.isChecked = true

                exoPlayer?.let {
                    viewModel.currentIndex = it.currentMediaItemIndex

                    mService?.let { mediaPlayerService ->
                        val audioItem = mediaPlayerService.audioItems[it.currentMediaItemIndex]
                        titleTextView.text = audioItem.title
                        viewModel.isAudioItemExist(mediaPlayerService.audioItems[exoPlayer?.currentMediaItemIndex!!])

                        Timber.d("args.surah.audios ${mediaPlayerService.audioItems.size} and it.currentMediaItemIndex ${it.currentMediaItemIndex}")
                    }

                }
            }

        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)

        if (playWhenReady) {
            isPlaying = true
            progressBar.max = exoPlayer?.duration?.toInt() ?: 0
            updateProgressBar()
            playCheckbox.isChecked = true

            exoPlayer?.let {
                mService?.let { mediaPlayerService ->
                    viewModel.currentIndex = it.currentMediaItemIndex
                    viewModel.isAudioItemExist(mediaPlayerService.audioItems[exoPlayer?.currentMediaItemIndex!!])
                }

            }

        } else {
            isPlaying = false
            playCheckbox.isChecked = false
        }
    }

    interface MediaPlayerFragmentStateListener {
        fun onServiceContected()
        fun onServiceDisContected()

    }
}