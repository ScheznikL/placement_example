package com.endofjanuary.placement_example.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ModelEntity::class,
    ],
    version = 4,
    exportSchema = false
)
abstract class ARAppDatabase : RoomDatabase() {
    abstract fun modelDao(): ModelEntityDao

}