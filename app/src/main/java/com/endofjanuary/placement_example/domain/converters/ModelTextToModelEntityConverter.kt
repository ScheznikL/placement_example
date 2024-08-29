package com.endofjanuary.placement_example.domain.converters

import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import com.endofjanuary.placement_example.data.room.ModelEntity

class ModelTextToModelEntityConverter {
    fun convertToModelEntity(
        model: TextTo3DModel,
        isFromText: Boolean = true,
        isRefine: Boolean = false
    ): ModelEntity {
        return ModelEntity(
            modelInstance = ByteArray(2), //todo get rid of Instance in table
            modelPath = model.model_urls.glb,
            modelDescription = model.prompt,
            modelImageUrl = model.thumbnail_url,
            isFromText = isFromText,
            isRefine = isRefine,
            meshyId = model.id,
            creationTime = System.currentTimeMillis()
        )
    }
}