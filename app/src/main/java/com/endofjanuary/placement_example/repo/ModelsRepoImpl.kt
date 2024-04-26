package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.data.room.ModelEntity
import com.endofjanuary.placement_example.data.room.ModelEntityDao
import com.endofjanuary.placement_example.utils.Resource

class ModelsRepoImpl(
    private val modelEntityDao: ModelEntityDao
) : ModelsRepo() {
    override suspend fun getAllModels(): Resource<List<ModelEntity>> {
        return try {
            val result = modelEntityDao.getAll()
            if (result.isNotEmpty()) {
                Resource.Success(result)
            } else {
                Resource.Error("empty List")
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun saveModel(modelEntity: ModelEntity): Resource<Long> {
        return try {
            val resultID = modelEntityDao.insert(modelEntity)
            Resource.Success(resultID)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun update(
        meshyId: String?,
        modelImageUrl: String?,
        modelPath: String?,
        isRefine: Boolean?
    ): Resource<Int> {
        return try {
            val res = modelEntityDao.updateModel(
                meshyId = meshyId,
                modelImageUrl = modelImageUrl,
                modelPath = modelPath,
                isRefine = isRefine
            )
            Resource.Success(res)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getModelById(modelId: Int): Resource<ModelEntity> {
        return try {
            val result = modelEntityDao.getModelById(modelId)
            if (result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("No model with corresponding $modelId")
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun deleteModelById(modelId: String): Resource<Int> {
        return try {
            val result = modelEntityDao.deleteModelById(modelId)
            if (result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("No model with corresponding $modelId")
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getLastModel(): Resource<ModelEntity> {
        return try {
            val result = modelEntityDao.getLastModel()
            if (result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("Error")
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }
}