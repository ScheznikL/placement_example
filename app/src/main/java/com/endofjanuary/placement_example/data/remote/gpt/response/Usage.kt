package com.endofjanuary.placement_example.data.remote.gpt.response

data class Usage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int
)