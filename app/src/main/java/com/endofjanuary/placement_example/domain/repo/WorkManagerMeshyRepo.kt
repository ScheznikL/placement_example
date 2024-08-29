package com.endofjanuary.placement_example.domain.repo

import androidx.work.WorkInfo
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromImage
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromText
import com.endofjanuary.placement_example.data.remote.meshy.request.PostRefine
import kotlinx.coroutines.flow.Flow

interface WorkManagerMeshyRepo {
    val outputGetModelWorkInfo: Flow<List<WorkInfo>>
    suspend fun getTextTo3D(id: String)
    suspend fun getRefine(id: String)
    fun postTextTo3D(body: PostFromText)
    suspend fun getImageTo3D(id: String)
    suspend fun postImageTo3D(body: PostFromImage)
    suspend fun postRefine(body: PostRefine)

}
