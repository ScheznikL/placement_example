package com.endofjanuary.placement_example.data.repoimpl

import android.content.Context
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.data.room.ModelEntity
import com.endofjanuary.placement_example.data.room.ModelEntityDao
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ModelsRepoImpl(
    private val modelEntityDao: ModelEntityDao,
    private val context: Context
) : ModelsRepo() {
    override val clearModelsTableError = MutableStateFlow("")

    override suspend fun getAllModelsFlow(): Flow<List<ModelEntity>> = modelEntityDao.getAll()
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
                Resource.Error(context.getString(R.string.room_no_model_error, modelId.toString()))
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getModelsById(vararg modelsIds: String): Resource<List<ModelEntity>> {
        return try {
            val result = modelEntityDao.getModelsById(*modelsIds)
            if (result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(context.getString(R.string.room_no_model_error, modelsIds.toString()))
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
                Resource.Error(context.getString(R.string.room_no_model_error, modelId))
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
                Resource.Error(context.getString(R.string.error))
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun deleteAll() {
        try {
            modelEntityDao.deleteAll()
        } catch (e: Exception) {
            clearModelsTableError.value = e.message.toString()
        }
    }
}

