package com.endofjanuary.placement_example

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class DownloaderImpl(
    private val context: Context
) : Downloader {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, modelName: String): Long {
        val req = DownloadManager.Request(url.toUri())
            //.setMimeType("model/glb")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("Model $modelName is downloading")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$modelName.glb")
        return downloadManager.enqueue(req)
    }


}