package com.endofjanuary.placement_example.di

import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.ar_screen.ARScreenViewModel
import com.endofjanuary.placement_example.chat.ChatScreenViewModel
import com.endofjanuary.placement_example.data.proto.appStartupParamsDataStore
import com.endofjanuary.placement_example.data.remote.gpt.AuthTokenGptInterseptor
import com.endofjanuary.placement_example.data.remote.gpt.ChatComplitionApi
import com.endofjanuary.placement_example.data.remote.meshy.AuthTokenInterceptor
import com.endofjanuary.placement_example.data.remote.meshy.MeshyApi
import com.endofjanuary.placement_example.loading.LoadingScreenViewModel
import com.endofjanuary.placement_example.models_list_screen.ModelsListViewModel
import com.endofjanuary.placement_example.register_screen.RegistrationViewModel
import com.endofjanuary.placement_example.repo.AWStorageRepo
import com.endofjanuary.placement_example.repo.AWStorageRepoImpl
import com.endofjanuary.placement_example.repo.AuthenticationRepo
import com.endofjanuary.placement_example.repo.AuthenticationRepoImpl
import com.endofjanuary.placement_example.repo.ChatRepo
import com.endofjanuary.placement_example.repo.ChatRepoImpl
import com.endofjanuary.placement_example.repo.DataStoreRepo
import com.endofjanuary.placement_example.repo.DataStoreRepoImpl
import com.endofjanuary.placement_example.repo.DownloaderRepo
import com.endofjanuary.placement_example.repo.DownloaderRepoImpl
import com.endofjanuary.placement_example.repo.FireStoreDBImpl
import com.endofjanuary.placement_example.repo.FireStoreDBRepo
import com.endofjanuary.placement_example.repo.MeshyRepo
import com.endofjanuary.placement_example.repo.MeshyRepoImpl
import com.endofjanuary.placement_example.three_d_screen.ThreeDScreenViewModel
import com.endofjanuary.placement_example.upload_image.UploadImageViewModel
import com.endofjanuary.placement_example.user_cabinet.UserProfileViewModel
import com.example.jetcaster.ui.home.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_MESHY_URL = "https://api.meshy.ai/"
private const val BASE_GPT_URL = "https://api.openai.com/v1/"

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
            .baseUrl(BASE_MESHY_URL)
            .build()
            .create(MeshyApi::class.java)
    }
    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()).client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        AuthTokenGptInterseptor()
                    ).build()
            )
            .baseUrl(BASE_GPT_URL)
            .build()
            .create(ChatComplitionApi::class.java)
    }
    single {
        Firebase.auth
    }
    single {
        androidApplication().applicationContext.appStartupParamsDataStore
    }
    single {
        Firebase.firestore
    }
    single<MeshyRepo> {
        MeshyRepoImpl(get())
    }
    single<FireStoreDBRepo> {
        FireStoreDBImpl(get())
    }
    single<ChatRepo> {
        ChatRepoImpl(get())
    }
    single<AWStorageRepo> {
        AWStorageRepoImpl()
    }
    single<AuthenticationRepo> {
        AuthenticationRepoImpl(get(), get())
    }
    single<DownloaderRepo> {
        DownloaderRepoImpl(androidContext().applicationContext)
    }
    single<DataStoreRepo> {
        DataStoreRepoImpl(get())
    }
    viewModel {
        HomeViewModel(get(), get())
    }
    viewModel {
        UserProfileViewModel(get(),get(),get())
    }
    viewModel {
        RegistrationViewModel(get())
    }
    viewModel {
        ARScreenViewModel(get())
    }
    viewModel {
        ThreeDScreenViewModel(get(), get())
    }
    viewModel {
        ChatScreenViewModel(get(), get())
    }
    viewModel {
        LoadingScreenViewModel(get(), get())
    }
    viewModel {
        ModelsListViewModel(get(), get())
    }
    viewModel {
        UploadImageViewModel(get())
    }
    viewModel {
        MainViewModel(
            context = androidContext(),
            meshyRepository = get(),
            modelRoom = get(),
            authenticationRepo = get()
        )
    }
    //   viewModel { (prompt: String) -> ARScreenViewModel(prompt, get()) }
//    viewModelOf(::ChatScreenViewModel)
}
