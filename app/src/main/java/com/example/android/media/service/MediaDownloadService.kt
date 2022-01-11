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
//
//    @Inject
//     var downloadManager: DownloadManager
//     @Inject
//     var downloadNotificationHelper: DownloadNotificationHelper


    override fun getDownloadManager(): DownloadManager {

       val databaseProvider = StandaloneDatabaseProvider(applicationContext)

        val downloadExecutor = Executor { obj: Runnable -> obj.run() }
        // A download cache should not evict media, so should use a NoopCacheEvictor.
        val downloadCache = SimpleCache(
            File(applicationContext.getExternalFilesDir(null), "my app"),
            NoOpCacheEvictor(),
            databaseProvider
        )

        // Create a factory for reading the data from the network.

        val dataSourceFactory = DefaultHttpDataSource.Factory()
        // Create the download manager.

        val  downloadManager = DownloadManager(
            applicationContext,
            databaseProvider,
            downloadCache,
            dataSourceFactory,
            downloadExecutor
        )

        // Optionally, setters can be called to configure the download manager.
        //downloadManager.setRequirements(requirements);
        downloadManager.setMaxParallelDownloads(3);


        downloadManager.addListener(object : DownloadManager.Listener {

            override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
                // Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
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

            }

        })

      return downloadManager
    }

    override fun getScheduler(): Scheduler? {
        return null
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        val downloadNotificationHelper = DownloadNotificationHelper(applicationContext,"Media Downloader")
        return downloadNotificationHelper.buildProgressNotification(this,R.drawable.icon, null, getString(R.string.channel_download_description), downloads)
    }
}