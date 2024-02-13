package com.endofjanuary.placement_example.data.models

data class ModelEntry (
    val modelPath: String,
    val modelImageUrl: String,
    val modelDescription: String,
){
    constructor(): this("models/model_v2_chair.glb","R.drawable.preview_model","chair")
}