package com.endofjanuary.placement_example.data.models

import com.endofjanuary.placement_example.data.converters.MessageType

data class MessageEntry(
    val messageType: MessageType,
    val content: String,
)