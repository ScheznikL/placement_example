package com.endofjanuary.placement_example

import android.app.Application
import androidx.room.Room
import com.endofjanuary.placement_example.data.room.ARAppDatabase
import com.endofjanuary.placement_example.di.appModule
import com.endofjanuary.placement_example.repo.ModelsRepo
import com.endofjanuary.placement_example.repo.ModelsRepoImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ARAppDatabase::class.java,
            "models.db"
        ).build()
    }
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@MainApplication)
            // Load modules
            modules(listOf(appModule,databaseModule))
        }

    }
    private val databaseModule = module{
        single { db.modelDao()}

        single<ModelsRepo> {
            ModelsRepoImpl(get())
        }
    }
}

