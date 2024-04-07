package com.endofjanuary.placement_example.data.converters

import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.data.models.ModelMode
import com.endofjanuary.placement_example.data.remote.meshy.responses.ImageTo3DModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.Refine3dModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel

class ResponseToModelEntryConverter {
    fun toModelEntry(modelfromtext: TextTo3DModel?): ModelEntry {
        // Log.d("toModelEntry",model?.model_urls?.glb ?: "none or error")
        return if (modelfromtext != null)
            ModelEntry(
                id = 0, // to Int Id that 018dc381-7336-7595-9d01-61ecaaa0ccde
                modelDescription = modelfromtext.prompt,
                modelPath = modelfromtext.model_urls.glb,
                modelImageUrl = modelfromtext.thumbnail_url,
                modelMode =  ModelMode.Preview,
                meshyId = modelfromtext.id
            )
        else
            ModelEntry()
    }
    fun toModelEntry(refineModel: Refine3dModel?): ModelEntry {
        // Log.d("toModelEntry",model?.model_urls?.glb ?: "none or error")
        return if (refineModel != null)
            ModelEntry(
                id = 0, // to Int Id that 018dc381-7336-7595-9d01-61ecaaa0ccde
                modelDescription = refineModel.prompt,
                modelPath = refineModel.model_urls.glb,
                modelImageUrl = refineModel.thumbnail_url,
                modelMode =  ModelMode.Refine,
                meshyId = refineModel.id
            )
        else
            ModelEntry()
    }

    fun toModelEntry(modelfromimage: ImageTo3DModel?, name: String = ""): ModelEntry {
        // Log.d("toModelEntry",model?.model_urls?.glb ?: "none or error")
        return if (modelfromimage != null)
            ModelEntry(
                id = 0, // to Int Id that 018dc381-7336-7595-9d01-61ecaaa0ccde
                modelDescription = name,
                modelPath = modelfromimage.model_urls.glb,
                modelImageUrl = modelfromimage.thumbnail_url,
                isFromText = false,
                meshyId = modelfromimage.id
            )
        else
            ModelEntry()
    }
}