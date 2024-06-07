package com.endofjanuary.placement_example.data.remote.meshy.responses

data class TextTo3DModel(
    val art_style: String,
    val created_at: Long,
    val finished_at: Long,
    val started_at: Long,
    val id: String,
    val model_urls: ModelUrls,
    val negative_prompt: String,
    val progress: Int,
    val prompt: String,
    val seed: Int,
    val status: String,
    val texture_urls: List<TextureUrl>,
    val thumbnail_url: String
)