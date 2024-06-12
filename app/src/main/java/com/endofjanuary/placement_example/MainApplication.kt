package com.endofjanuary.placement_example

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.endofjanuary.placement_example.data.room.ARAppDatabase
import com.endofjanuary.placement_example.di.appModule
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
import com.endofjanuary.placement_example.data.repoimpl.ModelsRepoImpl
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


