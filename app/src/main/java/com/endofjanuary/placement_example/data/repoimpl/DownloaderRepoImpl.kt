package com.endofjanuary.placement_example.data.repoimpl

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.endofjanuary.placement_example.domain.repo.DownloaderRepo
import com.endofjanuary.placement_example.utils.Resource

class DownloaderRepoImpl(
    private val context: Context
) : DownloaderRepo {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, modelName: String): Resource<Long> {
        return try {
            val req = DownloadManager.Request(url.toUri())
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(modelName)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "$modelName.glb"
                )
            val result = downloadManager.enqueue(req)
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

}