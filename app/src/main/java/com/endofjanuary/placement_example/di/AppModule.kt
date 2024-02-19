package com.endofjanuary.placement_example.di

import com.endofjanuary.placement_example.ar_screen.ARScreenViewModel
import com.endofjanuary.placement_example.data.remote.AuthTokenInterceptor
import com.endofjanuary.placement_example.data.remote.MeshyApi
import com.endofjanuary.placement_example.modelsList.ModelsListViewModel
import com.endofjanuary.placement_example.repo.MeshyRepo
import com.endofjanuary.placement_example.repo.MeshyRepoImpl
import com.endofjanuary.placement_example.three_d_screen.ThreeDScreenViewModel
import com.endofjanuary.placement_example.loading.LoadingScreenViewModel
import com.example.jetcaster.ui.home.HomeViewModel
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
                OkHttpClient.Builder()
                    // .connectTimeout(100, TimeUnit.SECONDS)
                    .addInterceptor(
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

    viewModel {
        ARScreenViewModel(get())
    }
    viewModel {
        ThreeDScreenViewModel(get())
    }
    viewModel {
        HomeViewModel()
    }
//    viewModel{
//        ChatScreenViewModel(get())
//    }

    viewModel {
        LoadingScreenViewModel(get(),get())
    }

    viewModel{
        ModelsListViewModel(get())
    }
    //   viewModel { (prompt: String) -> ARScreenViewModel(prompt, get()) }
//    viewModelOf(::ChatScreenViewModel)
}
