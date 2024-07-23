package com.endofjanuary.placement_example.domain.usecase.models_act

import com.endofjanuary.placement_example.domain.models.ModelEntry
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.hasFiveDaysPassed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.mapNotNull

class GetModelsUseCase(
    private val modelsRoomRepo: ModelsRepo,
) {
    suspend fun loadModels(): Flow<List<ModelEntry>> {
        return try {
            modelsRoomRepo.getAllModelsFlow().mapNotNull { models ->
                models.map { model ->
                    ModelEntry(
                        id = model.id,
                        modelPath = model.modelPath,
                        modelImageUrl = model.modelImageUrl,
                        modelDescription = model.modelDescription,
                        meshyId = model.meshyId,
                        isFromText = model.isFromText,
                        isExpired = hasFiveDaysPassed(model.creationTime)
                    )
                }
            }
        } catch (e: Exception) {
            emptyFlow()
        }
    }
}