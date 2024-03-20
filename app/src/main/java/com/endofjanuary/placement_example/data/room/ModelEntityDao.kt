package com.endofjanuary.placement_example.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
//TODO retrieve flow ?
@Dao
interface ModelEntityDao {
    @Query("SELECT * FROM models")
    fun getAll(): List<ModelEntity>
    @Query("SELECT * FROM models WHERE id = :modelId")
    fun getModelById(modelId: Int): ModelEntity?
    @Query("SELECT * FROM models ORDER BY id DESC LIMIT 1")
    fun getLastModel():ModelEntity?
    @Insert
    fun insertAll(vararg models: ModelEntity)
    @Delete
    fun delete(model: ModelEntity)
}