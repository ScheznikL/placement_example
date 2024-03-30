package com.endofjanuary.placement_example.data.remote.meshy.responses

data class ImageTo3DModel(
    val art_style: String,
    val created_at: Long,
    val expires_at: Long,
    val finished_at: Long,
    val id: String,
    val model_url: String,
    val model_urls: ModelUrlsX,
    val name: String,
    val negative_prompt: String,
    val object_prompt: String,
    val progress: Int,
    val started_at: Long,
    val status: String,
    val style_prompt: String,
    val task_error: Any,
    val texture_urls: Any,
    val thumbnail_url: String
)