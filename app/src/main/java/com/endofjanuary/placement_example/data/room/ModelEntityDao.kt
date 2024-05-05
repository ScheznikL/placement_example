package com.endofjanuary.placement_example.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//TODO retrieve flow ?
@Dao
interface ModelEntityDao {
    @Query("SELECT * FROM models")
    fun getAll(): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE id = :modelId")
    fun getModelById(modelId: Int): ModelEntity?

    @Query("SELECT * FROM models ORDER BY id DESC LIMIT 1")
    fun getLastModel(): ModelEntity?

    @Query("UPDATE models SET modelImageUrl = :modelImageUrl, isRefine = :isRefine, modelPath= :modelPath WHERE meshyId =:meshyId")
    fun updateModel(
        meshyId: String?,
        modelImageUrl: String?,
        modelPath: String?,
        isRefine: Boolean?
    ): Int

    @Query("DELETE FROM models WHERE meshyId =:meshyId")
    fun deleteModelById(meshyId: String?): Int?

    @Insert
    fun insert(model: ModelEntity): Long

    @Delete
    fun delete(model: ModelEntity)
}