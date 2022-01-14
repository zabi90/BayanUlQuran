package com.example.android.injection.modules

import android.content.Context
import com.example.android.R
import com.example.android.mangers.DownloadMediaManager
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class MediaPlayerModule {

    @Provides
    @Singleton
    fun getDownloaderDataBase(@ApplicationContext applicationContext: Context): StandaloneDatabaseProvider {
        return StandaloneDatabaseProvider(applicationContext)
    }

    @Provides
    @Singleton
    fun getDownloadCache(@ApplicationContext applicationContext: Context, databaseProvider: StandaloneDatabaseProvider) : SimpleCache{
        return SimpleCache(
            File(applicationContext.getExternalFilesDir(null),applicationContext.getString(R.string.app_name)),
            NoOpCacheEvictor(),
            databaseProvider
        )
    }

    @Provides
    @Singleton
    fun getDefaultHttpDataSource(): DefaultHttpDataSource.Factory {
       return DefaultHttpDataSource.Factory()
    }


    @Provides
    @Singleton
    fun getDownloadManager(@ApplicationContext applicationContext: Context, databaseProvider: StandaloneDatabaseProvider,downloadCache : SimpleCache ): DownloadManager {

        val downloadExecutor = Executor { obj: Runnable -> obj.run() }
        // A download cache should not evict media, so should use a NoopCacheEvictor.

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

        return downloadManager
    }

    @Provides
    @Singleton
    fun getNotification(@ApplicationContext applicationContext: Context):DownloadNotificationHelper{
       return DownloadNotificationHelper(applicationContext,"Media Downloader")
    }

    @Provides
    fun getDownloadMediaManager(@ApplicationContext applicationContext: Context,downloadManager: DownloadManager ) : DownloadMediaManager{
        return  DownloadMediaManager(applicationContext,downloadManager)
    }
}