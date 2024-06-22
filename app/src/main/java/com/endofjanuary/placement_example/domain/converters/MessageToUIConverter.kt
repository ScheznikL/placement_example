package com.endofjanuary.placement_example.domain.converters

import com.endofjanuary.placement_example.data.remote.gpt.response.Message
import com.endofjanuary.placement_example.domain.models.MessageEntry

class MessageToUIConverter {
    fun toMessageEntry(message: Message): MessageEntry {
        val messageType =
            if (message.content.uppercase().contains("FINAL")) MessageType.Final
            else {
                when (message.role) {
                    "user" -> {
                        MessageType.User
                    }

                    "assistant" -> {
                        MessageType.Assistant
                    }

                    "autorefine" -> {
                        MessageType.AutoRefine
                    }

                    "error" -> {
                        MessageType.Error
                    }

                    else -> {
                        MessageType.Loading
                    }
                }
            }
        return MessageEntry(
            content = message.content,
            messageType = messageType
        )
    }
}

enum class MessageType {
    User,
    Assistant,
    Final,
    Loading,
    AutoRefine,
    Error
}
