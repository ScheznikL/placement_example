package com.endofjanuary.placement_example.data.converters

import android.util.Log
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.data.remote.responses.TextTo3DModel

class ResponseToModelEntryConverter {
    fun toModelEntry(model: TextTo3DModel?): ModelEntry {
        Log.d("toModelEntry",model?.model_urls?.glb ?: "none or error")
        return if (model != null)
            ModelEntry(
                id = 0, // to Int Id that 018dc381-7336-7595-9d01-61ecaaa0ccde
                modelDescription = model.prompt,
                modelPath = model.model_urls.glb,
                modelImageUrl = model.thumbnail_url
            )
        else
            ModelEntry()
    }
}