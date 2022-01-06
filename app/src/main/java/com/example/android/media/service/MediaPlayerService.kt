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
import com.example.android.R
import com.example.android.models.AudioItem
import com.example.android.ui.activities.MainActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class MediaPlayerService : Service() {

    private val binder = LocalBinder()
    var isPlaying: Boolean = false
    var exoPlayer: ExoPlayer? = null


    var audioItems: List<AudioItem> = mutableListOf()

    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
    }


    fun setMediaItem(audioItems: List<AudioItem>) {

        if (this.audioItems != audioItems) {

            this.audioItems = audioItems

            exoPlayer?.let {

                if (it.isPlaying) {
                    it.stop()
                    // it.release()
                    it.clearMediaItems()
                }

                // Build the media item.
                audioItems.forEachIndexed { index, audioItem ->

                    val mediaItem: MediaItem =
                        MediaItem.fromUri("http://download1.quranurdu.com/Bayan%20ul%20Quran%20in%20Urdu%20Dr%20Asrar%20Ahmed/"+audioItem.url)

                    // Set the media item to be played.
                    it.addMediaItem(mediaItem)

                }

                // Prepare the player.
                it.prepare()
                // Start the playback.
                it.play()
            }
        }else{
            exoPlayer?.pause()
            exoPlayer?.play()
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
                    return audioItems[player.currentMediaItemIndex] .title
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
                    return audioItems[player.currentMediaItemIndex].title
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

        playerNotificationManager.setPlayer(exoPlayer)
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlayerService {
            return this@MediaPlayerService
        }
    }


    override fun onDestroy() {
        exoPlayer?.release()
        exoPlayer = null
        playerNotificationManager.setPlayer(null)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}