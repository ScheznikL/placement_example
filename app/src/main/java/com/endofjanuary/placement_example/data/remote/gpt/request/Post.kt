package com.endofjanuary.placement_example.data.remote.gpt.request

import com.endofjanuary.placement_example.data.remote.gpt.response.Message

data class Post(
    //val max_tokens: Int,
    val messages: List<Message>,
    val model: String = "gpt-3.5-turbo",
    val temperature: Double = 0.7
)