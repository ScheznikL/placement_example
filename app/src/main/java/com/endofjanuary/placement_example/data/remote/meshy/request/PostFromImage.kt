package com.endofjanuary.placement_example.data.remote.meshy.request

data class PostFromImage(
    val image_url: String, //.jpg / .jpeg / .png
    val enable_pbr: Boolean = true,
)
