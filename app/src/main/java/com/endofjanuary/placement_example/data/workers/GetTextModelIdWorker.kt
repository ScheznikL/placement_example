package com.endofjanuary.placement_example.data.workers

import DESCR_KEY
import ERROR_KEY
import ID_KEY
import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.endofjanuary.placement_example.data.remote.meshy.MeshyApi
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromText
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GetTextModelIdWorker(/*private val meshyRepository: MeshyRepo,*/ ctx: Context,
                           params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    companion object {
        private const val TAG = "GetTextModelIdWorker"
        fun schedule(workManager: WorkManager) {
            val request = OneTimeWorkRequestBuilder<GetTextModelIdWorker>().setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).addTag(TAG).build()
            workManager.enqueueUniqueWork(TAG, ExistingWorkPolicy.KEEP, request)
        }
    }

    override suspend fun doWork(): Result {
        try {

            val descriptionString = inputData.getString(DESCR_KEY) ?: return Result.failure(
                workDataOf(
                    DESCR_KEY to "is null"
                )
            )
            //  val resultId: Resource<String> = Resource.Error("none")
            val resultId = withContext(Dispatchers.IO) {
                return@withContext try {
                    val postFromText = Gson().fromJson(descriptionString, PostFromText::class.java)
                    MeshyApi.instance.postTextTo3D(postFromText) // TODO not network Resourse ?

                } catch (throwable: Throwable) {
                    Log.e(
                        "CommonModel", "${throwable.message}", throwable
                    )
                    throw throwable
                }
            }

            /*            if (resultId is Resource.Error) {
                            val outputData = workDataOf(ERROR_KEY to resultId.message)
                            return Result.failure(outputData)
                        }

                        if (resultId.data == null) return Result.failure(workDataOf(ID_KEY to "is null"))

            *//*
            val outputData = workDataOf(ID_KEY to resultId.data.result)
*/  Log.d(
                "CommonModel", "success id - ${resultId.result}"
            )

            val outputData = workDataOf(ID_KEY to resultId.result)
            return Result.success(outputData)
        } catch (error: Throwable) {
            val outputData = workDataOf(ERROR_KEY to error.message.toString())
            return Result.failure(outputData)
        }
    }
}