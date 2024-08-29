package com.endofjanuary.placement_example.data.workers

import ERROR_KEY
import ID_KEY
import MODEL_KEY
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.endofjanuary.placement_example.data.remote.meshy.responses.ProgressStatus
import com.endofjanuary.placement_example.domain.repo.MeshyRepo
import com.endofjanuary.placement_example.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetTextModelWorker(
    appContext: Context, params: WorkerParameters, private val meshyRepository: MeshyRepo
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        try {
            val modelId = inputData.getString(ID_KEY) ?: return Result.failure()

            val model = withContext(Dispatchers.IO) {
                return@withContext try {
                    meshyRepository.getTextTo3D(modelId)
                } catch (throwable: Throwable) {/*  Log.e(
                      TAG,
                      applicationContext.resources.getString(R.string.error_applying_blur),
                      throwable
                  )*/
                    throw throwable
                }
            }

            if (model is Resource.Error) {
                val outputData = workDataOf(ERROR_KEY to model.message)
                return Result.failure(outputData)
            }

            if (model is Resource.Success) {
                val modelStatus = model.data?.status ?: ProgressStatus.FAILED.toString()

                val status = checkForNegativeStatus(modelStatus)
                if (status == null) {
                    val outputData = workDataOf(MODEL_KEY to Gson().toJson(model.data))
                    Result.success(outputData)
                } else {
                    return status
                }
            }

            if (model is Resource.Loading || model is Resource.None) return Result.retry()

            return Result.failure()

        } catch (error: Throwable) {
            val outputData = workDataOf(ERROR_KEY to error.message.toString())
            return Result.failure(outputData)
        }
    }

    private fun checkForNegativeStatus(modelStatus: String): Result? {

        val pending = modelStatus == ProgressStatus.PENDING.toString()
        val inProgress = modelStatus == ProgressStatus.IN_PROGRESS.toString()

        if (pending || inProgress) {
            return Result.retry()
        }
        val expired = modelStatus == ProgressStatus.EXPIRED.toString()
        val failed = modelStatus == ProgressStatus.FAILED.toString()

        if (expired || failed) {
            val outputData = workDataOf(ERROR_KEY to modelStatus)
            return Result.failure(outputData)
        }

        return null
    }
}

