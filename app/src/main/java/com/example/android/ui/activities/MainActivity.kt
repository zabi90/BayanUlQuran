package com.example.android.ui.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.android.R
import com.example.android.base.BaseActivity
import com.example.android.base.BaseViewModel
import com.example.android.databinding.ActivityMainBinding
import com.example.android.media.service.MediaPlayerService
import com.example.android.models.Surah
import com.example.android.ui.fragments.FavouriteFragmentDirections
import com.example.android.ui.fragments.HomeFragmentDirections
import com.example.android.ui.fragments.MediaPlayerFragment
import com.example.android.viewmodels.MainViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : BaseActivity(), Player.Listener,
    MediaPlayerFragment.MediaPlayerFragmentStateListener {
    companion object {
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var binding: ActivityMainBinding
    private var exoPlayer: ExoPlayer? = null
    var mService: MediaPlayerService? = null
    var mBound: Boolean = false
    lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()
    //region Activity overrides methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView3) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        //viewModel.loadFeeds().observe(this, {
        //binding.titleTextView.text = it.toString()
        // })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (!mBound) {
            Intent(this, MediaPlayerService::class.java).also { intent ->
                this.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
            Timber.d("Main Activity : Service bind")
        }

    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        navController.removeOnDestinationChangedListener(listener)
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(connection)
            mBound = false
            Timber.d("Main Activity : unbindService")
        }
    }

    // endregion

    //region Base class and interface override methods
    override fun setListeners() {
        binding.mediaView.setOnClickListener {

            navController.currentDestination?.let { destination->
                val surah = Surah(-1, "", listOf())
                if (destination.label.contentEquals("Bayan ul Quran", ignoreCase = true)) {


                    val action = HomeFragmentDirections.actionHomeFragmentToMediaPlayerFragment(surah)
                    navController.navigate(action)

                }else if(destination.label.contentEquals("Favourite", ignoreCase = true)){

                    val action = FavouriteFragmentDirections.actionFavouriteFragmentToMediaPlayerFragment(surah)
                    navController.navigate(action)
                }
            }
        }

        binding.playCheckBox.setOnClickListener {
            exoPlayer?.let { player ->
                if (player.isPlaying)
                    player.pause()
                else
                    player.play()
            }
        }

    }

    override fun setViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        binding.playCheckBox.isChecked = isPlaying

        mService?.let { mediaPlayerService ->

            exoPlayer?.let { player ->
                if (isPlaying) {

                    navController.currentDestination?.let { destination ->

                        if (destination.label.contentEquals("Media Player", ignoreCase = true)) {
                            binding.mediaView.visibility = View.GONE
                        }else{
                            binding.mediaView.visibility = View.VISIBLE
                        }
                    }
                    binding.titleTextView.text =
                        mediaPlayerService.audioItems[player.currentMediaItemIndex].title
                }
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        when (playbackState) {
            Player.STATE_BUFFERING -> {

            }
            Player.STATE_ENDED -> {

            }
            Player.STATE_IDLE -> {
                if (mBound) {
                    binding.mediaView.visibility = View.GONE
                    unbindService(connection)
                    mBound = false
                    Timber.d("Main Activity : Player.STATE_IDLE unbindService ")
                }
            }
            Player.STATE_READY -> {

            }

        }
    }

    private val listener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            // react on change
            // you can check destination.id or destination.label and act based on that
            if (destination.label.contentEquals("Media Player", ignoreCase = true)) {
                binding.mediaView.visibility = View.GONE

            } else {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        binding.mediaView.visibility = View.VISIBLE
                    }
                }
            }
        }

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
                exoPlayer?.let { player ->

                    if (player.isPlaying) {
                        binding.mediaView.visibility = View.VISIBLE
                        binding.titleTextView.text =
                            mediaPlayerService.audioItems[player.currentMediaItemIndex].title
                    } else {
                        binding.mediaView.visibility = View.GONE
                    }

                    binding.playCheckBox.isChecked = player.isPlaying
                }

                exoPlayer?.addListener(this@MainActivity)

            }
            mBound = true
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Timber.d("Media Player Fragment onServiceDisconnected")
            exoPlayer?.removeListener(this@MainActivity)
            mBound = false
        }
    }

    override fun onServiceContected() {
        if (!mBound) {
            Intent(this, MediaPlayerService::class.java).also { intent ->
                this.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    override fun onServiceDisContected() {

    }

    //endregion
}
