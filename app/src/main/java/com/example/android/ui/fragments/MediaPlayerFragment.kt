package com.example.android.ui.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
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
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.R
import com.example.android.base.BaseFragment
import com.example.android.base.BaseViewModel
import com.example.android.extensions.showSnackBar
import com.example.android.mangers.DownloadMediaManager
import com.example.android.media.service.MediaPlayerService
import com.example.android.ui.activities.MainActivity
import com.example.android.viewmodels.MediaViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlayerFragment : BaseFragment(), Player.Listener,
    DownloadMediaManager.DownloadMediaListener {
    private val viewModel: MediaViewModel by viewModels()
    private var exoPlayer: ExoPlayer? = null
    private val handler: Handler = Handler()


    var isPlaying: Boolean = false
    var mService: MediaPlayerService? = null
    var mBound: Boolean = false
    private var dragging: Boolean = false

    private val args: MediaPlayerFragmentArgs by navArgs()
    lateinit var playCheckbox: AppCompatCheckBox
    lateinit var favouriteCheckBox: AppCompatCheckBox
    lateinit var checkBoxDownload: AppCompatImageView
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
    lateinit var loadingProgressBar: ProgressBar
    lateinit var downloadingProgressBar: ProgressBar

    var mediaPlayerFragmentStateListener: MediaPlayerFragmentStateListener? = null

    @Inject
    lateinit var downloadMediaManager: DownloadMediaManager


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
                    val audioItem = args.surah.audios[exoPlayer?.currentMediaItemIndex!!]
                    viewModel.isAudioItemExist(audioItem)
                    viewModel.currentAudioItem = audioItem
                    exoPlayer?.playWhenReady = true

                } else {
                    mService?.setMediaItem(mediaPlayerService.audioItems)
                    val audioItem =
                        mediaPlayerService.audioItems[exoPlayer?.currentMediaItemIndex!!]
                    viewModel.isAudioItemExist(audioItem)
                    viewModel.currentAudioItem = audioItem
                    exoPlayer?.playWhenReady = true
                }

                viewModel.isAudioDownloaded()


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_media_player, container, false)

        playCheckbox = view.findViewById(R.id.play_checkbox)
        progressBar = view.findViewById(R.id.progressBar)
        downloadingProgressBar = view.findViewById(R.id.progress_bar_download)
        loadingProgressBar = view.findViewById(R.id.loading_progress)
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
        checkBoxDownload = view.findViewById(R.id.image_view_download)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { context ->

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


            viewModel.isFavourite.observe(viewLifecycleOwner, {
                favouriteCheckBox.isChecked = it
            })

            checkBoxDownload.setOnClickListener {
                downloadMediaManager.sendRequest(viewModel.currentAudioItem)
                checkBoxDownload.isEnabled = false
            }
        }
    }

    override fun setListeners() {

        downloadMediaManager.downloadMediaListener = this

        viewModel.isDownloaded.observe(this, { status ->

            when (status) {
                Download.STATE_COMPLETED -> {
                    downloadingProgressBar.visibility = View.GONE
                    checkBoxDownload.setImageResource(R.drawable.ic_baseline_cloud_done_24)
                    checkBoxDownload.isEnabled = false
                }
                Download.STATE_DOWNLOADING -> {
                    downloadingProgressBar.visibility = View.VISIBLE
                    checkBoxDownload.setImageResource(R.drawable.ic_baseline_cloud_download_24)
                    checkBoxDownload.isEnabled = false
                }
                else -> {
                    checkBoxDownload.setImageResource(R.drawable.ic_baseline_cloud_download_24)
                    checkBoxDownload.isEnabled = true
                }
            }
        })
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
        }
    }

    override fun onStop() {
        super.onStop()
        activity
            ?.unbindService(connection)
    }

    override fun onDetach() {
        super.onDetach()
        handler.removeCallbacks(updateProgressAction)
        exoPlayer?.removeListener(this@MediaPlayerFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MediaPlayerFragmentStateListener) {
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
                Timber.d("onPlaybackStateChanged ${Player.STATE_BUFFERING}")
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

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        // toast(error.cause?.message)
        loadingProgressBar.visibility = View.GONE
        showSnackBar("Error while playing.", Color.RED)
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
        if (isLoading) {
            loadingProgressBar.visibility = View.VISIBLE
        } else {
            loadingProgressBar.visibility = View.GONE
        }
    }


    override fun onProgressChanged(download: Download, progress: Long) {
        downloadingProgressBar.visibility = View.VISIBLE
    }

    override fun onError(download: Download, finalException: Exception) {
        showSnackBar("Error while downloading audio : ${download.request.id}.")
    }

    override fun onCompleted(download: Download) {

        showSnackBar("Downloading completed : audio : ${download.request.id}.")
        viewModel.isAudioDownloaded()
    }

    override fun onPaused() {

    }

    override fun onResumed() {

    }

    override fun onRemoved(download: Download) {

    }

    interface MediaPlayerFragmentStateListener {
        fun onServiceContected()
        fun onServiceDisContected()

    }
}