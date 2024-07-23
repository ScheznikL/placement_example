package com.endofjanuary.placement_example.di

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import com.endofjanuary.placement_example.AwsAccessKeyId
import com.endofjanuary.placement_example.AwsSecretAccessKey
import com.endofjanuary.placement_example.AwsSessionToken
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.data.proto.appStartupParamsDataStore
import com.endofjanuary.placement_example.data.remote.gpt.AuthTokenGptInterseptor
import com.endofjanuary.placement_example.data.remote.gpt.ChatCompletionApi
import com.endofjanuary.placement_example.data.remote.meshy.AuthTokenInterceptor
import com.endofjanuary.placement_example.data.remote.meshy.MeshyApi
import com.endofjanuary.placement_example.data.repoimpl.AWStorageRepoImpl
import com.endofjanuary.placement_example.data.repoimpl.ApiService
import com.endofjanuary.placement_example.data.repoimpl.AuthenticationRepoImpl
import com.endofjanuary.placement_example.data.repoimpl.ChatRepoImpl
import com.endofjanuary.placement_example.data.repoimpl.DataStoreRepoImpl
import com.endofjanuary.placement_example.data.repoimpl.DownloaderRepoImpl
import com.endofjanuary.placement_example.data.repoimpl.MeshyRepoImpl
import com.endofjanuary.placement_example.domain.repo.AWStorageRepo
import com.endofjanuary.placement_example.domain.repo.AuthenticationRepo
import com.endofjanuary.placement_example.domain.repo.ChatRepo
import com.endofjanuary.placement_example.domain.repo.DataStoreRepo
import com.endofjanuary.placement_example.domain.repo.DownloaderRepo
import com.endofjanuary.placement_example.domain.repo.MeshyRepo
import com.endofjanuary.placement_example.domain.usecase.GetPreassignedUrlUseCase
import com.endofjanuary.placement_example.domain.usecase.SendMessageUseCase
import com.endofjanuary.placement_example.domain.usecase.models_act.GenerateModelFromTextUseCase
import com.endofjanuary.placement_example.ui.screens.chat.ChatScreenViewModel
import com.endofjanuary.placement_example.ui.screens.home_screen.HomeViewModel
import com.endofjanuary.placement_example.ui.screens.models_list_screen.ModelsListViewModel
import com.endofjanuary.placement_example.ui.screens.register_screen.RegistrationViewModel
import com.endofjanuary.placement_example.ui.screens.upload_image.UploadImageViewModel
import com.endofjanuary.placement_example.ui.screens.user_cabinet.UserProfileViewModel
import com.endofjanuary.placement_example.ui.screens.visualize_screens.VisualizeViewModel
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
private const val AWS_REGION = "eu-north-1"

val appModule = module {
    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()).client(
                OkHttpClient.Builder()
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
            .create(ChatCompletionApi::class.java)
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
    single {
        S3Client {
            region = AWS_REGION
            credentialsProvider = StaticCredentialsProvider {
                this.accessKeyId = AwsAccessKeyId
                this.secretAccessKey = AwsSecretAccessKey
                this.sessionToken = AwsSessionToken
            }
        }
    }

    single<MeshyRepo> {
        MeshyRepoImpl(get())
    }
    /**
     * TODO later
     */
    ////////////////////////////
    single {
        ApiService()
    }
    single<ChatRepo> {
        ChatRepoImpl(get(),get())
    }
    //////////////////////////////////

    single<AWStorageRepo> {
        AWStorageRepoImpl(get())
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
    single{
        GetPreassignedUrlUseCase(get())
    }
    single{
        SendMessageUseCase(get())
    }
    single {
        GenerateModelFromTextUseCase(get(),get())
    }
    viewModel {
        HomeViewModel(get(), get())
    }
    viewModel {
        UserProfileViewModel(get(), get(), get())
    }
    viewModel {
        RegistrationViewModel(get())
    }
    viewModel {
        VisualizeViewModel(get(), get())
    }
    viewModel {
        ChatScreenViewModel(get())
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
            authenticationRepo = get(),
            dataStoreRepo = get(),
            generateModelFromText = get()
        )
    }
}
