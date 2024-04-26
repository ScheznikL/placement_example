package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.utils.Resource
import java.io.InputStream

interface AWStorageRepo {
    suspend fun putPresignedS3Object(objectPath: String, stream: InputStream)
    suspend fun putObject(content: String, stream: InputStream)
    suspend fun putObjectPresigned(content: String, stream: InputStream): Resource<String>

    //suspend fun putBitmapObjectPresigned(name: String, stream: InputStream): Resource<String>
}