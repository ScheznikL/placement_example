package com.endofjanuary.placement_example.data.remote.meshy.responses

data class Refine3dModel(
    val art_style: String,
    val created_at: Long,
    val finished_at: Long,
    val id: String,
    val mode: String,
    val model_urls: ModelUrls,
    val name: String,
    val negative_prompt: String,
    val progress: Int,
    val prompt: String,
    val seed: Int,
    val started_at: Long,
    val status: String,
    val task_error: Any,
    val texture_richness: String,
    val texture_urls: List<TextureUrl>,
    val thumbnail_url: String,
    val video_url: String
)