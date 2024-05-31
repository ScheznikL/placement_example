package com.endofjanuary.placement_example.data.remote.gpt.response

data class Message(
    val content: String,
    val role: String? = null
)