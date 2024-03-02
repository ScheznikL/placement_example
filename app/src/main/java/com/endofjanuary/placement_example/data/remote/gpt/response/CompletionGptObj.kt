package com.endofjanuary.placement_example.data.remote.gpt.response

data class CompletionGptObj(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val system_fingerprint: String,
    val usage: Usage
)
