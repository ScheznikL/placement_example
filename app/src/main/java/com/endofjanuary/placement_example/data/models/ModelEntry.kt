package com.endofjanuary.placement_example.data.models

data class ModelEntry (
    val id: Int,
    val modelPath: String,
    val modelImageUrl: String,
    val modelDescription: String,
){
    constructor(): this(0,"models/model_v2_chair.glb","R.drawable.preview_model","chair")
}