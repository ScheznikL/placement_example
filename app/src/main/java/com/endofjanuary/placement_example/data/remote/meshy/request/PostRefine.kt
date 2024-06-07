package com.endofjanuary.placement_example.data.remote.meshy.request

data class PostRefine(
    val preview_task_id: String,
    val texture_richness: String,
    val mode: String = "refine",
)