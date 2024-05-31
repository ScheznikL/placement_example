package com.endofjanuary.placement_example.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.data.converters.MessageType
import com.endofjanuary.placement_example.data.models.MessageEntry
import com.example.gffcompose.composables.chat.OutgoingMessageBubble

@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    message: MessageEntry,
    onEdit: () -> Unit,
    onDone: () -> Unit,
    onRefineCancel: () -> Unit,
    onGetRefineOptions: (texture: MainViewModel.TextureRichness) -> Unit,
) {
    when (message.messageType) {
        MessageType.User -> OutgoingMessageBubble(
            modifier = modifier,
            message = message.content,
        )

        MessageType.Assistant -> IncomingMessageBubble(
            modifier = modifier,
            message = message.content,
        )

        MessageType.Final -> FinalMessageBubble(
            modifier = modifier,
            message = message.content,
            onEdit, onDone
        )

        MessageType.Loading -> ModelLoadingBubble()

        MessageType.AutoRefine ->
            AutoRefineProgressBubble(
            modifier = modifier,
            message = message.content,
            onDone = onGetRefineOptions,
            onCancel = onRefineCancel
        )
    }
}