package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.data.room.ModelEntity
import com.endofjanuary.placement_example.utils.Resource

abstract class ModelsRepo {
    abstract suspend fun getAllModels(): Resource<List<ModelEntity>>
    abstract suspend fun saveModel(modelEntity: ModelEntity): Resource<Boolean>
    abstract suspend fun getModelById(modelId: Int): Resource<ModelEntity>
}
