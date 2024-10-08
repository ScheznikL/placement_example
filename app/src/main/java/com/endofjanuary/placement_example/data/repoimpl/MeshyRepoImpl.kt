package com.endofjanuary.placement_example.data.repoimpl

import android.util.Log
import com.endofjanuary.placement_example.data.remote.meshy.MeshyApi
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromImage
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromText
import com.endofjanuary.placement_example.data.remote.meshy.request.PostRefine
import com.endofjanuary.placement_example.data.remote.meshy.responses.ImageTo3DModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.PostId
import com.endofjanuary.placement_example.data.remote.meshy.responses.Refine3dModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import com.endofjanuary.placement_example.domain.repo.MeshyRepo
import com.endofjanuary.placement_example.utils.Resource

class MeshyRepoImpl(
    private val api: MeshyApi
) : MeshyRepo {
    override suspend fun getTextTo3D(id: String): Resource<TextTo3DModel> {
        val response = try {
            api.getTextTo3D(id)
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }

    override suspend fun getRefine(id: String): Resource<Refine3dModel> {
        val response = try {
            api.getRefine(id)
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }

    override suspend fun postTextTo3D(body: PostFromText): Resource<PostId> {
        Log.e("CommonModel", "post Enter")
        val response = try {
            api.postTextTo3D(body)
        } catch (e: Exception) {
            Log.e("CommonModel", e.message.toString())
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }

    override suspend fun postTextTo3DWorker(body: PostFromText): String {
        val response = try {
            api.postTextTo3D(body)
        } catch (e: Exception) {
            return e.message.toString()
        }
        return response.result
    }

    override suspend fun getImageTo3D(id: String): Resource<ImageTo3DModel> {
        val response = try {
            api.getImageTo3D(id)
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }

    override suspend fun postImageTo3D(body: PostFromImage): Resource<PostId> {
        val response = try {
            api.postImageTo3D(body)
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }

    override suspend fun postRefine(body: PostRefine): Resource<PostId> {
        val response = try {
            api.postRefine(body)
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }
}