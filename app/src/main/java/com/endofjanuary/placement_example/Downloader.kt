package com.endofjanuary.placement_example

interface Downloader {
    fun downloadFile(url: String, modelName: String): Long
}