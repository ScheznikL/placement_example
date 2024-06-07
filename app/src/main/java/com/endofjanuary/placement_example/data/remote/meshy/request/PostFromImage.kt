package com.endofjanuary.placement_example.data.remote.meshy.request

data class PostFromImage(
    val image_url: String,
    val enable_pbr: Boolean = true,
)
