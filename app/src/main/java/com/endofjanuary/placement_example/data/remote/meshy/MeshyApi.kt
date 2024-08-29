package com.endofjanuary.placement_example.data.remote.meshy

import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromImage
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromText
import com.endofjanuary.placement_example.data.remote.meshy.request.PostRefine
import com.endofjanuary.placement_example.data.remote.meshy.responses.ImageTo3DModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.PostId
import com.endofjanuary.placement_example.data.remote.meshy.responses.Refine3dModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface MeshyApi {
    @POST("v2/text-to-3d")
    suspend fun postTextTo3D(
        @Body body: PostFromText,
    ): PostId

    @GET("v2/text-to-3d/{id}")
    suspend fun getTextTo3D(@Path("id") id: String): TextTo3DModel

    @GET("v2/text-to-3d/{id}")
    suspend fun getRefine(@Path("id") id: String): Refine3dModel

    @POST("v2/text-to-3d")
    suspend fun postRefine(@Body body: PostRefine): PostId

    @POST("v1/image-to-3d")
    suspend fun postImageTo3D(
        @Body body: PostFromImage,
    ): PostId

    @GET("v1/image-to-3d/{id}")
    suspend fun getImageTo3D(@Path("id") id: String): ImageTo3DModel

    companion object {
        val instance by lazy {
            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()).client(
                    OkHttpClient.Builder()
                        .addInterceptor(
                            AuthTokenInterceptor()
                        ).build()
                )
                .baseUrl("https://api.meshy.ai/")
                .build()
                .create(MeshyApi::class.java)
        }
    }
}