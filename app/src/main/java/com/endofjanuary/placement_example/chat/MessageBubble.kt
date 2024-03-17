package com.endofjanuary.placement_example.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gffcompose.composables.chat.OutgoingMessageBubble

@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    message: com.endofjanuary.placement_example.data.remote.gpt.response.Message,
) {
    when (message.role) {
        "user" -> OutgoingMessageBubble(
            modifier = modifier,
            message = message.content,
        )

        else -> IncomingMessageBubble(
            modifier = modifier,
            message = message.content,
        )
    }
}