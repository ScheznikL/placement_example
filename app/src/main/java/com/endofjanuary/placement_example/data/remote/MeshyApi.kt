package com.endofjanuary.placement_example.data.remote

import com.endofjanuary.placement_example.data.remote.request.Post
import com.endofjanuary.placement_example.data.remote.responses.PostRes
import com.endofjanuary.placement_example.data.remote.responses.TextTo3DModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface MeshyApi {
    @POST("text-to-3d")
    suspend fun postTextTo3D(
        @Body body: Post,
    ): PostRes

    @GET("text-to-3d/{id}")
    suspend fun getTextTo3D(@Path("id") id: String): TextTo3DModel
}