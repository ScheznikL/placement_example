package com.endofjanuary.placement_example.data.remote.gpt.response

data class Choice(
    val finish_reason: String,
    val index: Int,
    val logprobs: Any,
    val message: Message
)