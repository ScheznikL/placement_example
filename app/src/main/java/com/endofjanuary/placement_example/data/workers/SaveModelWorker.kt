package com.endofjanuary.placement_example.data.workers

import ERROR_KEY
import MODEL_KEY
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import com.endofjanuary.placement_example.domain.converters.ModelTextToModelEntityConverter
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveModelWorker(
    appContext: Context, params: WorkerParameters, private val roomRepo: ModelsRepo
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        try {
            val modelJson = inputData.getString(MODEL_KEY) ?: return Result.failure()

            val model =
                Gson().fromJson(modelJson, TextTo3DModel::class.java) ?: return Result.failure()
            val entity = ModelTextToModelEntityConverter().convertToModelEntity(model)

            val result = withContext(Dispatchers.IO) {
                return@withContext try {
                    roomRepo.saveModel(entity)
                } catch (throwable: Throwable) {/*  Log.e(
                      TAG,
                      applicationContext.resources.getString(R.string.error_applying_blur),
                      throwable
                  )*/
                    throw throwable
                }
            }

            if (result is Resource.Error) {
                val outputData = workDataOf(ERROR_KEY to result.message)
                return Result.failure(outputData)
            }

            return Result.success()

        } catch (error: Throwable) {
            val outputData = workDataOf(ERROR_KEY to error.message.toString())
            return Result.failure(outputData)
        }
    }
}