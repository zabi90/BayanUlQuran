package com.example.android.mangers

import android.content.Context
import android.net.Uri
import com.example.android.BuildConfig
import com.example.android.media.service.MediaDownloadService
import com.example.android.models.AudioItem
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import timber.log.Timber
import javax.inject.Inject

class DownloadMediaManager @Inject constructor(
    val context: Context,
    val downloadManager: DownloadManager
):  DownloadManager.Listener {

    var downloadMediaListener : DownloadMediaListener? = null

    init {
        downloadManager.addListener(this)
    }


    fun sendRequest(audioItem: AudioItem) {

        val downloadRequest: DownloadRequest = DownloadRequest.Builder(
            audioItem.title,
            Uri.parse(BuildConfig.AUDIO_URL + audioItem.url)
        ).build()


        DownloadService.sendAddDownload(
            context,
            MediaDownloadService::class.java,
            downloadRequest,
            /* foreground= */ true
        )

    }

    fun cancelRequest(audioItem: AudioItem) {

    }

    fun isMediaDownloaded(audioItem: AudioItem): Boolean {

        var isExist = false

        val downloadCursor = downloadManager.downloadIndex.getDownloads()

        if (downloadCursor.moveToFirst()) {
            do {
                val id = downloadCursor.download.request.id
                if (audioItem.title.contentEquals(id, ignoreCase = true)) {
                    isExist = true
                    break
                }
            } while (downloadCursor.moveToNext())
        }
        return isExist
    }

    override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
        downloadMediaListener?.onRemoved(download)
    }

    override fun onDownloadsPausedChanged(downloadManager: DownloadManager, downloadsPaused: Boolean) {
        if (downloadsPaused){

        } else{

        }

    }

    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?
    ) {
        if(finalException != null){
            downloadMediaListener?.onError(download,finalException)
        }else if(download.bytesDownloaded == download.contentLength){
            downloadMediaListener?.onCompleted(download)
        }
        downloadMediaListener?.onProgressChanged(download,(download.bytesDownloaded * 100 / download.contentLength))
        //Toast.makeText(this@MediaDownloadService, "percentDownloaded ${download.percentDownloaded } , finalException ${finalException?.message} ", Toast.LENGTH_SHORT).show()
        Timber.d("onDownloadChanged bytesDownloaded :${download.bytesDownloaded}, download.contentLength ${download.contentLength}, download.percentDownloaded ${download.percentDownloaded },${download.toString()} ")
    }

     interface DownloadMediaListener{
         fun onProgressChanged(download: Download,progress : Long)
         fun onError(download: Download,finalException: Exception)
         fun onCompleted(download: Download)
         fun onPaused()
         fun onResumed()
         fun onRemoved(download: Download)
     }
}