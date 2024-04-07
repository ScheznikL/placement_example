package com.endofjanuary.placement_example.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ModelEntity::class,
    ],
    version = 3,
    exportSchema = false
)
//@TypeConverters(Converters::class)
abstract class ARAppDatabase : RoomDatabase() {
    abstract fun modelDao(): ModelEntityDao
//    abstract fun episodesDao(): EpisodesDao
//    abstract fun categoriesDao(): CategoriesDao
//    abstract fun podcastCategoryEntryDao(): PodcastCategoryEntryDao
//    abstract fun transactionRunnerDao(): TransactionRunnerDao
//    abstract fun podcastFollowedEntryDao(): PodcastFollowedEntryDao
}