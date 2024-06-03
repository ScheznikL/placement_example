package com.endofjanuary.placement_example.repo

import android.util.Log
import androidx.datastore.core.DataStore
import com.endofjanuary.placement_example.LastModelsParam
import com.endofjanuary.placement_example.ModelAccessParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreRepoImpl(
    private val dataStore: DataStore<LastModelsParam>
) : DataStoreRepo {

    override val dataStoreState = MutableStateFlow("")
    override val dataStoreData: Flow<LastModelsParam>
        get() = dataStore.data

    override suspend fun updateData(modelId: String, id: Int, modelImageUrl: String) {
        Log.d(
            "modelList dataStore model _rewrite",
            "start start ${dataStore.data.map { it.lastModelsList }}"
        )
        try {
            dataStore.updateData { currentSettings ->
                if (currentSettings.lastModelsCount >= 10) {
                    currentSettings.toBuilder().removeLastModels(0).addLastModels(
                        //count,
                        ModelAccessParam.newBuilder().setModelId(modelId).setId(id)
                            .setModelImage(modelImageUrl)
                            .setUnixTimestamp(System.currentTimeMillis())
                    ).build()
                } else {
                    currentSettings.toBuilder().addLastModels(
                        currentSettings.lastModelsCount,
                        ModelAccessParam.newBuilder().setModelId(modelId).setId(id)
                            .setModelImage(modelImageUrl)
                            .setUnixTimestamp(System.currentTimeMillis())
                    ).build()
                }
            }
        } catch (e: Exception) {
            dataStoreState.value = e.message.toString()
        }
    }

    override suspend fun removeModelById(modelId: String) {

        val modelsList = dataStore.data.first().lastModelsList
        val index = modelsList.indexOfFirst { it.modelId == modelId }
        try {
            dataStore.updateData { currentSettings ->
                currentSettings.toBuilder().removeLastModels(index).build()
            }
        } catch (e: Exception) {
            dataStoreState.value = e.message.toString()
        }
    }

    override suspend fun clearDataStore() {
        try {
            dataStore.updateData { current ->
                current.toBuilder().clear().build()
            }
        } catch (e: Exception) {
            dataStoreState.value = e.message.toString()
        }
    }

    override suspend fun clearModelsInDataStore() {
        try {
            dataStore.updateData { currentSettings ->
                currentSettings.toBuilder().clearLastModels().build()
            }
        } catch (e: Exception) {
            dataStoreState.value = e.message.toString()
        }

    }

}