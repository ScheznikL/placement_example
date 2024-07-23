package com.endofjanuary.placement_example.domain.usecase.models_act

import androidx.compose.runtime.mutableStateOf
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromText
import com.endofjanuary.placement_example.data.remote.meshy.responses.ProgressStatus
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import com.endofjanuary.placement_example.domain.converters.ResponseToModelEntryConverter
import com.endofjanuary.placement_example.domain.models.ModelEntry
import com.endofjanuary.placement_example.domain.repo.MeshyRepo
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.delay

class GenerateModelFromTextUseCase(
    private val meshyRepository: MeshyRepo,
    private val modelRoomRepo: ModelsRepo,
) {
    val model = mutableStateOf(ModelEntry())
    private suspend fun getTextTo3D(id: String): Resource<TextTo3DModel> {
        return meshyRepository.getTextTo3D(id)
    }

    suspend fun loadModelEntryFromText(
        prompt: String, delayTime: Long = 20000, autoRefine: Boolean
    ): Resource<ModelEntry?> {
        val result = meshyRepository.postTextTo3D(PostFromText(prompt, "preview"))
       when (result) {
            is Resource.Error -> return Resource.Error(result.message!!)
            is Resource.Loading -> return Resource.Loading()
            is Resource.None -> return Resource.None()
            is Resource.Success -> {
                //if (result.data != null) {
                    var modelStatus = getTextTo3D(result.data!!.result)
                    when (modelStatus) {
                        is Resource.Success -> {
                            while (modelStatus.data!!.status == ProgressStatus.PENDING.toString()
                                || modelStatus.data!!.status == ProgressStatus.IN_PROGRESS.toString()
                            ) {
                                delay(delayTime)
                                modelStatus = getTextTo3D(result.data.result)
                                if (modelStatus is Resource.Error) {
                                    return Resource.Error(modelStatus.message!!)
                                }
                            }
                            if (modelStatus.data!!.status == ProgressStatus.SUCCEEDED.toString()) {
                                return Resource.Success(
                                    ResponseToModelEntryConverter().toModelEntry(
                                        modelStatus.data
                                    )
                                )
                            }
                            if (modelStatus.data!!.status == ProgressStatus.FAILED.toString()
                                || modelStatus.data!!.status == ProgressStatus.EXPIRED.toString()
                            ) {
                                return Resource.Error(message = modelStatus.data!!.status)
                            }
                            return Resource.Loading()
                        }

                        is Resource.Error -> return Resource.Error(modelStatus.message!!)
                        is Resource.Loading -> return Resource.Loading()
                        is Resource.None -> return Resource.None()
                    }
               // }
            }
        }
    }
}