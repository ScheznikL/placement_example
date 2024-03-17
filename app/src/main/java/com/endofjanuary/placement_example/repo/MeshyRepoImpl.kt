package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.data.remote.meshy.MeshyApi
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromImage
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromText
import com.endofjanuary.placement_example.data.remote.meshy.responses.ImageTo3DModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.PostId
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import com.endofjanuary.placement_example.utils.Resource

class MeshyRepoImpl (
    private val api : MeshyApi
) : MeshyRepo {
    override suspend fun getTextTo3D(id: String): Resource<TextTo3DModel> {
        val response = try {
            api.getTextTo3D(id)
        } catch(e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }

    override suspend fun postTextTo3D(body: PostFromText): Resource<PostId> {
        val response = try {
            api.postTextTo3D(body)
        } catch(e: Exception) {
            return Resource.Error("error - ${e.message.toString()}")
        }
        return Resource.Success(response)
    }

    override suspend fun getImageTo3D(id: String): Resource<ImageTo3DModel> {
        val response = try {
            api.getImageTo3D(id)
        } catch(e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }

    override suspend fun postImageTo3D(body: PostFromImage): Resource<PostId> {
        val response = try {
            api.postImageTo3D(body)
        } catch(e: Exception) {
            return Resource.Error("error - ${e.message.toString()}")
        }
        return Resource.Success(response)
    }
}