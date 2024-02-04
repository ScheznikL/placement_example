package com.endofjanuary.placement_example.di

import com.endofjanuary.placement_example.data.remote.MeshyApi
import com.endofjanuary.placement_example.repo.MeshyRepo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.meshy.ai/v2"

object AppModule {
    fun provideMeshyRepo(
        api: MeshyApi
    ) = MeshyRepo(api)

    fun provideMeshyApi(): MeshyApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(MeshyApi::class.java)
    }
}