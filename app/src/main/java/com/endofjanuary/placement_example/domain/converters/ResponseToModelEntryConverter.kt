package com.endofjanuary.placement_example.domain.converters

import com.endofjanuary.placement_example.domain.models.ModelEntry
import com.endofjanuary.placement_example.domain.models.ModelMode
import com.endofjanuary.placement_example.data.remote.meshy.responses.ImageTo3DModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.Refine3dModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import com.endofjanuary.placement_example.utils.hasThreeDaysPassed

class ResponseToModelEntryConverter {
    fun toModelEntry(modelfromtext: TextTo3DModel?): ModelEntry {

        return if (modelfromtext != null)
            ModelEntry(
                id = 0,
                modelDescription = modelfromtext.prompt,
                modelPath = modelfromtext.model_urls.glb,
                modelImageUrl = modelfromtext.thumbnail_url,
                modelMode =  ModelMode.Preview,
                meshyId = modelfromtext.id,
                isExpired = hasThreeDaysPassed(modelfromtext.created_at)
            )
        else
            ModelEntry()
    }
    fun toModelEntry(refineModel: Refine3dModel?): ModelEntry {

        return if (refineModel != null)
            ModelEntry(
                id = 0,
                modelDescription = refineModel.prompt,
                modelPath = refineModel.model_urls.glb,
                modelImageUrl = refineModel.thumbnail_url,
                modelMode =  ModelMode.Refine,
                meshyId = refineModel.id,
                isExpired = hasThreeDaysPassed(refineModel.created_at)
            )
        else
            ModelEntry()
    }

    fun toModelEntry(modelfromimage: ImageTo3DModel?, name: String = ""): ModelEntry {

        return if (modelfromimage != null)
            ModelEntry(
                id = 0,
                modelDescription = name,
                modelPath = modelfromimage.model_urls.glb,
                modelImageUrl = modelfromimage.thumbnail_url,
                isFromText = false,
                meshyId = modelfromimage.id,
                isExpired = hasThreeDaysPassed(modelfromimage.created_at)
            )
        else
            ModelEntry()
    }
}