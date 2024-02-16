package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.data.remote.MeshyApi
import com.endofjanuary.placement_example.data.remote.request.Post
import com.endofjanuary.placement_example.data.remote.responses.PostId
import com.endofjanuary.placement_example.data.remote.responses.TextTo3DModel
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

    override suspend fun postTextTo3D(body: Post): Resource<PostId> {
        val response = try {
            api.postTextTo3D(body)
        } catch(e: Exception) {
            return Resource.Error("error - ${e.message.toString()}")
        }
        return Resource.Success(response)
    }
}