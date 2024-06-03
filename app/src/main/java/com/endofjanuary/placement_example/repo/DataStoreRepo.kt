package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.LastModelsParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface DataStoreRepo {
    val dataStoreData: Flow<LastModelsParam>
    suspend fun updateData(modelId: String, id: Int, modelImageUrl: String)
    suspend fun clearDataStore()

    suspend fun clearModelsInDataStore()


    val dataStoreState: MutableStateFlow<String>
    suspend fun removeModelById(modelId: String)
}