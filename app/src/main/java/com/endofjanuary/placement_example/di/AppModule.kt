package com.endofjanuary.placement_example.di

import com.endofjanuary.placement_example.ar_screen.ARScreenViewModel
import com.endofjanuary.placement_example.data.remote.AuthTokenInterceptor
import com.endofjanuary.placement_example.data.remote.MeshyApi
import com.endofjanuary.placement_example.repo.MeshyRepo
import com.endofjanuary.placement_example.repo.MeshyRepoImpl
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.meshy.ai/v2/"

val appModule = module {
    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()).client(
                OkHttpClient.Builder().addInterceptor(
                    AuthTokenInterceptor()
                ).build()
            )
            .baseUrl(BASE_URL)
            .build()
            .create(MeshyApi::class.java)
    }
    single<MeshyRepo> {
        MeshyRepoImpl(get())
    }
    /*single<LocalStorageRepo> {
        LocalStorageRepoImpl()
    }*/
    /* viewModel{
         ARScreenViewModel(get(),get())
     }*/
    viewModel { parameters ->
        //ARScreenViewModel(prompt = parameters.get(), get())
        ARScreenViewModel(get())
    }
    //   viewModel { (prompt: String) -> ARScreenViewModel(prompt, get()) }
//    viewModelOf(::ChatScreenViewModel)
}
/*
    fun provideMeshyRepo(
        api: MeshyApi
    ) = MeshyRepoImpl(api)

    fun provideMeshyApi(): MeshyApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()).client(
                OkHttpClient.Builder().addInterceptor(
                    AuthTokenInterceptor()
                ).build()
            )
            .baseUrl(BASE_URL)
            .build()
            .create(MeshyApi::class.java)
    }
}*/