package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.utils.Resource

interface DownloaderRepo {
    fun downloadFile(url: String, modelName: String): Resource<Long>
}