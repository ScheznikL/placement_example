package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromImage
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromText
import com.endofjanuary.placement_example.data.remote.meshy.responses.ImageTo3DModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.PostId
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import com.endofjanuary.placement_example.utils.Resource

interface MeshyRepo {
    suspend fun getTextTo3D(id: String): Resource<TextTo3DModel>
    suspend fun postTextTo3D(body: PostFromText): Resource<PostId>
    suspend fun getImageTo3D(id: String): Resource<ImageTo3DModel>
    suspend fun postImageTo3D(body: PostFromImage): Resource<PostId>
}
