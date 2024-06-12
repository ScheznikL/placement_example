package com.endofjanuary.placement_example.domain.models

import com.endofjanuary.placement_example.domain.converters.MessageType

data class MessageEntry(
    val messageType: MessageType,
    val content: String,
)