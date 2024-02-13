package com.endofjanuary.placement_example.repo

interface LocalStorageRepo {
    suspend fun saveModel(url: String): String
   // suspend fun loadModel(): Resource<ModelEntry>
}