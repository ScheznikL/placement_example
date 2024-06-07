package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.utils.Resource
import java.io.InputStream

interface AWStorageRepo {
    suspend fun putObjectPresigned(content: String, stream: InputStream): Resource<String>
}