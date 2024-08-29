package com.endofjanuary.placement_example.domain.usecase.models_act

import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromImage
import com.endofjanuary.placement_example.data.remote.meshy.responses.ProgressStatus
import com.endofjanuary.placement_example.domain.converters.ResponseToModelEntryConverter
import com.endofjanuary.placement_example.domain.models.ModelEntry
import com.endofjanuary.placement_example.domain.repo.MeshyRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

class GenerateModelFromImageUseCase(
    private val meshyRepository: MeshyRepo,
) {
    var progress = MutableStateFlow<Int?>(null)
        private set

    private suspend fun getImageTo3D(id: String): Resource<ModelEntry?> {

        var modelStatus = meshyRepository.getImageTo3D(id)

        when (modelStatus) {
            is Resource.Error -> return Resource.Error(modelStatus.message!!)
            is Resource.None -> return Resource.None()
            is Resource.Success -> {
                while (modelStatus.data!!.status == ProgressStatus.PENDING.toString()
                    || modelStatus.data!!.status == ProgressStatus.IN_PROGRESS.toString()
                ) {
                    progress.value = modelStatus.data!!.progress
                    delay(40000)
                    modelStatus = meshyRepository.getImageTo3D(id)
                    if (modelStatus is Resource.Error) {
                        return Resource.Error(modelStatus.data!!.task_error.toString())
                    }
                }
                if (modelStatus.data!!.status == ProgressStatus.SUCCEEDED.toString()) {
                    return Resource.Success(
                        ResponseToModelEntryConverter().toModelEntry(
                            modelStatus.data!!
                        )
                    )
                }
                //if (modelStatus.data!!.status == ProgressStatus.FAILED.toString() || modelStatus.data!!.status == ProgressStatus.EXPIRED.toString()) {
                return Resource.Error(modelStatus.data!!.task_error.toString())
                //}
            }
            else -> return Resource.Loading(
                ResponseToModelEntryConverter().toModelEntry(
                    modelStatus.data!!
                )
            )
        }
    }

    suspend fun loadModelEntryFromImage(url: String, name: String = ""): Resource<ModelEntry?> {

        val resultId = meshyRepository.postImageTo3D(PostFromImage(url))
        when (resultId) {
            is Resource.Success -> {
                return getImageTo3D(resultId.data!!.result)
            }

            is Resource.Error -> return Resource.Error(resultId.message!!)
            is Resource.Loading -> return Resource.Loading()
            is Resource.None -> return Resource.None()
        }
    }
}