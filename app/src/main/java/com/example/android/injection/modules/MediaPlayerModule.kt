//package com.example.android.injection.modules
//
//import android.content.Context
//import com.example.android.R
//import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
//import com.google.android.exoplayer2.offline.DownloadManager
//import com.google.android.exoplayer2.ui.DownloadNotificationHelper
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
//import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
//import com.google.android.exoplayer2.upstream.cache.SimpleCache
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ServiceComponent
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import java.io.File
//import java.util.concurrent.Executor
//import javax.inject.Singleton
//
//
//@Module
//@InstallIn(ServiceComponent::class)
//class MediaPlayerModule {
//
//    @Provides
//    @Singleton
//    fun getDownloaderDataBase(@ApplicationContext applicationContext: Context): StandaloneDatabaseProvider {
//        return StandaloneDatabaseProvider(applicationContext)
//    }
//
//
//    @Provides
//    @Singleton
//    fun getDownloadManager(@ApplicationContext applicationContext: Context, databaseProvider: StandaloneDatabaseProvider): DownloadManager {
//
//        val downloadExecutor = Executor { obj: Runnable -> obj.run() }
//        // A download cache should not evict media, so should use a NoopCacheEvictor.
//        val downloadCache = SimpleCache(
//            File(applicationContext.getExternalFilesDir(null), "my app"),
//            NoOpCacheEvictor(),
//            databaseProvider
//        )
//
//        // Create a factory for reading the data from the network.
//
//       val dataSourceFactory = DefaultHttpDataSource.Factory()
//        // Create the download manager.
//
//       val  downloadManager = DownloadManager(
//           applicationContext,
//            databaseProvider,
//            downloadCache,
//            dataSourceFactory,
//            downloadExecutor
//        )
//
//        // Optionally, setters can be called to configure the download manager.
//        //downloadManager.setRequirements(requirements);
//        downloadManager.setMaxParallelDownloads(3);
//
//        return downloadManager
//    }
//
//    @Provides
//    @Singleton
//    fun getNotification(@ApplicationContext applicationContext: Context):DownloadNotificationHelper{
//       return DownloadNotificationHelper(applicationContext,"Media Downloader")
//    }
//}