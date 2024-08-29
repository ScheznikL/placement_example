package com.endofjanuary.placement_example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.endofjanuary.placement_example.data.repoimpl.ModelsRepoImpl
import com.endofjanuary.placement_example.data.room.ARAppDatabase
import com.endofjanuary.placement_example.di.appModule
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
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
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(appModule, databaseModule))
        }
    }

    private val databaseModule = module {
        single { db.modelDao() }

        single<ModelsRepo> {
            ModelsRepoImpl(get(),applicationContext)
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = MainViewModel.CHANNEL_NEW_MODEL
            val descriptionText = applicationContext.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(MainViewModel.CHANNEL_NEW_MODEL, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE models ADD COLUMN isFromText INTEGER DEFAULT 0 NOT NULL")
        db.execSQL("ALTER TABLE models ADD COLUMN isRefine INTEGER DEFAULT 0 NOT NULL")
    }
}
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE models ADD COLUMN meshyId TEXT DEFAULT '' NOT NULL")
    }
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE models ADD COLUMN creationTime INTEGER NOT NULL DEFAULT 0")
    }
}


