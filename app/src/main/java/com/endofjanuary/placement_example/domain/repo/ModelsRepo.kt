package com.endofjanuary.placement_example.domain.repo

import com.endofjanuary.placement_example.data.room.ModelEntity
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class ModelsRepo {
    abstract suspend fun getAllModelsFlow(): Flow<List<ModelEntity>>
    abstract suspend fun saveModel(modelEntity: ModelEntity): Resource<Long>
    abstract suspend fun update(meshyId: String?, modelImageUrl: String?, modelPath: String?, isRefine: Boolean?): Resource<Int>
    abstract suspend fun getModelById(modelId: Int): Resource<ModelEntity>
    abstract suspend fun getModelsById(vararg modelsIds: String): Resource<List<ModelEntity>>
    abstract suspend fun deleteModelById(modelId: String):  Resource<Int>
    abstract suspend fun getLastModel(): Resource<ModelEntity>
    abstract suspend fun deleteAll()

    abstract val clearModelsTableError: MutableStateFlow<String>
}
