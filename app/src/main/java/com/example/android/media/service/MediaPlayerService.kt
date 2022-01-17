package com.example.android.media.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.android.BuildConfig
import com.example.android.R
import com.example.android.models.AudioItem
import com.example.android.models.Surah
import com.example.android.ui.activities.MainActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache


@AndroidEntryPoint
class MediaPlayerService : Service() {

    private val binder = LocalBinder()

    lateinit var exoPlayer: ExoPlayer

    var audioItems: List<AudioItem> = mutableListOf()


  @Inject lateinit var myDownloadManager:DownloadManager

  @Inject lateinit var downloadCache : SimpleCache

  private var playerNotificationManager: PlayerNotificationManager? = null

    override fun onCreate() {
        super.onCreate()


        val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory()
            .setCache(downloadCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setCacheWriteDataSinkFactory(null) // Disable writing.


        exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(cacheDataSourceFactory)
            )
            .build()
    }

    fun setMediaItem(audioItems: List<AudioItem>) {


        if (this.audioItems != audioItems ) {

            this.audioItems = audioItems
            if (exoPlayer.isPlaying) {
                exoPlayer.stop()
                // it.release()
                exoPlayer.clearMediaItems()
            }
            // Build the media item.
            this.audioItems.forEachIndexed { index, audioItem ->

                val mediaItem: MediaItem =
                    MediaItem.fromUri(BuildConfig.AUDIO_URL + audioItem.url)
                // Set the media item to be played.
                exoPlayer.addMediaItem(mediaItem)
            }
            // Prepare the player.
            exoPlayer.prepare()
            // Start the playback.
            exoPlayer.play()

        } else {
            exoPlayer.pause()
            exoPlayer.play()
        }

        playerNotificationManager = PlayerNotificationManager.Builder(this, 13, "Media channel")
            .setChannelNameResourceId(R.string.channel_name)
            .setChannelDescriptionResourceId(R.string.channel_description)
            .setChannelImportance(NotificationCompat.PRIORITY_DEFAULT)
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    super.onNotificationPosted(notificationId, notification, ongoing)
                    startForeground(notificationId, notification)
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    super.onNotificationCancelled(notificationId, dismissedByUser)
                    stopSelf()
                }

            })
            .setMediaDescriptionAdapter(object :
                PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return this@MediaPlayerService.audioItems[player.currentMediaItemIndex].title
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    var pendingIntent: PendingIntent? = null
                    pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.getActivity(
                            this@MediaPlayerService,
                            0,
                            Intent(this@MediaPlayerService, MainActivity::class.java),
                            PendingIntent.FLAG_MUTABLE
                        )
                    } else {
                        PendingIntent.getActivity(
                            this@MediaPlayerService,
                            0,
                            Intent(this@MediaPlayerService, MainActivity::class.java),
                            PendingIntent.FLAG_ONE_SHOT
                        )
                    }
                    return pendingIntent

                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return this@MediaPlayerService.audioItems[player.currentMediaItemIndex].title
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return BitmapFactory.decodeResource(
                        resources,
                        R.drawable.icon
                    );
                }

            })
            .build()

        playerNotificationManager?.setPlayer(exoPlayer)
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlayerService {
            return this@MediaPlayerService
        }
    }


    override fun onDestroy() {
        exoPlayer.release()
        playerNotificationManager?.setPlayer(null)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}