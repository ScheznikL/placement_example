package com.endofjanuary.placement_example.data.models

data class ModelEntry (
    val id: Int,
    val modelPath: String,
    val modelImageUrl: String,
    val modelDescription: String,
    val progress: Int = 0,
    val isFromText: Boolean = true,
    val modelMode : ModelMode = ModelMode.Preview
){
    constructor(): this(0,"models/model_v2_chair.glb","R.drawable.preview_model","", )
}

enum class ModelMode{
    Preview,
    Refine
}