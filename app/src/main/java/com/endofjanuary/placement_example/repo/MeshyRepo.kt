package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.data.remote.MeshyApi
import com.endofjanuary.placement_example.data.remote.request.Post
import com.endofjanuary.placement_example.data.remote.responses.PostRes
import com.endofjanuary.placement_example.data.remote.responses.RedusedAPIResp
import com.endofjanuary.placement_example.utils.Resource

class MeshyRepo(
    private val api : MeshyApi
) {
    suspend fun getTextTo3D(id: String): Resource<RedusedAPIResp> {
        val response = try {
            api.getTextTo3D(id)
        } catch(e: Exception) {
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }

    suspend fun postTextTo3D(body: Post): Resource<PostRes> {
        val response = try {
            api.postTextTo3D(body)
        } catch(e: Exception) {
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }
}