package com.endofjanuary.placement_example.repo

import kotlinx.coroutines.flow.MutableStateFlow

interface DataStoreRepo {
    suspend fun saveData()
    suspend fun clearDataStore()
    val dataStoreState: MutableStateFlow<String>
}