package com.example.android.media.service

import android.app.Notification
import android.widget.Toast
import com.example.android.R
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.lang.Exception
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class MediaDownloadService  : DownloadService(312, DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,"Media Downloader",
    R.string.channel_download_name,R.string.channel_download_description) {

     @Inject  lateinit var myDownloadManager: DownloadManager

     @Inject lateinit var downloadNotificationHelper: DownloadNotificationHelper


    override fun getDownloadManager(): DownloadManager {

        myDownloadManager.addListener(object : DownloadManager.Listener {

            override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
                Toast.makeText(this@MediaDownloadService, "Deleted", Toast.LENGTH_SHORT).show()
            }

            override fun onDownloadsPausedChanged(downloadManager: DownloadManager, downloadsPaused: Boolean) {
                if (downloadsPaused){
                    Toast.makeText(this@MediaDownloadService, "paused", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this@MediaDownloadService, "resumed", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onDownloadChanged(
                downloadManager: DownloadManager,
                download: Download,
                finalException: Exception?
            ) {
                Toast.makeText(this@MediaDownloadService, "percentDownloaded ${download.percentDownloaded } , finalException ${finalException?.message} ", Toast.LENGTH_SHORT).show()
            }

        })

      return myDownloadManager
    }

    override fun getScheduler(): Scheduler? {
        return null
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {

        return downloadNotificationHelper.buildProgressNotification(this,R.drawable.icon, null, getString(R.string.channel_download_description), downloads)
    }
}