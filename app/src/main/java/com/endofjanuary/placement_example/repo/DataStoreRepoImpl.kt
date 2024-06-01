package com.endofjanuary.placement_example.repo

import androidx.datastore.core.DataStore
import com.endofjanuary.placement_example.LastModelsParam
import kotlinx.coroutines.flow.MutableStateFlow

class DataStoreRepoImpl(
    private val dataStore: DataStore<LastModelsParam>
) : DataStoreRepo {

    override val dataStoreState = MutableStateFlow("")
    override suspend fun saveData() {
        TODO("Not yet implemented")
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

}