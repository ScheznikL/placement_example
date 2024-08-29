package com.endofjanuary.placement_example.data.repoimpl

import DESCR_KEY
import GET_MODEL_WORKER
import TAG_GET_MODEL
import TAG_GET_MODEL_ID
import TAG_MODEL
import TAG_SAVE_MODEL
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromImage
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromText
import com.endofjanuary.placement_example.data.remote.meshy.request.PostRefine
import com.endofjanuary.placement_example.data.workers.GetTextModelIdWorker
import com.endofjanuary.placement_example.data.workers.GetTextModelWorker
import com.endofjanuary.placement_example.data.workers.SaveModelWorker
import com.endofjanuary.placement_example.domain.repo.WorkManagerMeshyRepo
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.util.concurrent.TimeUnit

class WorkManagerLoadTextModelRepoImpl(
    private val workManager: WorkManager,
    override val outputGetModelWorkInfo: Flow<List<WorkInfo>> = workManager.getWorkInfosByTagFlow(
        TAG_MODEL
    ).mapNotNull {
        if (it.isNotEmpty()) it.toList() else null
    }
) : WorkManagerMeshyRepo {

   // var workId:MutableState<UUID?> = mutableStateOf(null)

   // val info = workManager.getWorkInfoByIdLiveData(workId.value!!).observeAsState()
    fun cancelFetchingModelPeriodically() {
        workManager.cancelAllWorkByTag(TAG_GET_MODEL_ID)
    }

    override suspend fun getTextTo3D(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getRefine(id: String) {
        TODO("Not yet implemented")
    }

    override fun postTextTo3D(body: PostFromText) {
        val data = Data.Builder().putString(DESCR_KEY, Gson().toJson(body)).build()
        val getModelIdReq = OneTimeWorkRequestBuilder<GetTextModelIdWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).setInputData(data)
            .addTag(TAG_GET_MODEL_ID)
            .addTag(TAG_MODEL)
            .build()

        val getModel = OneTimeWorkRequestBuilder<GetTextModelWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).setBackoffCriteria(
            BackoffPolicy.LINEAR, 20, TimeUnit.SECONDS
        ).addTag(TAG_GET_MODEL).addTag(TAG_MODEL).build()

        val saveModelReq =
            OneTimeWorkRequestBuilder<SaveModelWorker>().addTag(TAG_SAVE_MODEL).addTag(TAG_MODEL).build()

        val res = workManager.beginUniqueWork(
            GET_MODEL_WORKER, ExistingWorkPolicy.KEEP, getModelIdReq
        ).then(getModel).then(saveModelReq).enqueue()
    }

    override suspend fun getImageTo3D(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun postImageTo3D(body: PostFromImage) {
        TODO("Not yet implemented")
    }

    override suspend fun postRefine(body: PostRefine) {
        TODO("Not yet implemented")
    }
}