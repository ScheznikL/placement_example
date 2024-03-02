package com.endofjanuary.placement_example.data.remote.meshy

import com.endofjanuary.placement_example.data.remote.meshy.request.Post
import com.endofjanuary.placement_example.data.remote.meshy.responses.PostId
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface MeshyApi {
    @POST("text-to-3d")
    suspend fun postTextTo3D(
        @Body body: Post,
    ): PostId

    @GET("text-to-3d/{id}")
    suspend fun getTextTo3D(@Path("id") id: String): TextTo3DModel
}