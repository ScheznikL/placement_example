package com.endofjanuary.placement_example.domain.usecase

import com.endofjanuary.placement_example.data.remote.gpt.request.Post
import com.endofjanuary.placement_example.data.remote.gpt.response.Message
import com.endofjanuary.placement_example.domain.converters.MessageToUIConverter
import com.endofjanuary.placement_example.domain.converters.MessageType
import com.endofjanuary.placement_example.domain.models.MessageEntry
import com.endofjanuary.placement_example.domain.repo.ChatRepo
import com.endofjanuary.placement_example.ui.screens.chat.assistantMessage
import com.endofjanuary.placement_example.ui.screens.chat.deviantMarkerPhrase
import com.endofjanuary.placement_example.ui.screens.chat.markerPhrase
import com.endofjanuary.placement_example.ui.screens.chat.questionWords
import com.endofjanuary.placement_example.ui.screens.chat.specificIsMarker
import com.endofjanuary.placement_example.ui.screens.chat.systemMessage
import com.endofjanuary.placement_example.utils.Resource

class SendMessageUseCase(
    private val chatRepository: ChatRepo
) {
    var description: String? = null
    private var fullUserMessage = ""
    private val errorMessage = "The message is empty"

    private val converter = MessageToUIConverter()
    suspend fun send(userMessageContext: String): MessageEntry {
        description = userMessageContext
        fullUserMessage += "$userMessageContext "
        val result = //chatRepository.postToGpt(Post(messages = composeMessages()))
          chatRepository.testPostToGpt(Post(messages = composeMessages()))

        when (result) {
            is Resource.Success -> {
                if (result.data != null) {
                    if (!result.data.choices[0].message.content.contains(markerPhrase)) {
                        fullUserMessage += "${extractQuestionObj(result.data.choices[0].message.content)}: "
                    } else {
                        description = extractFullDescription(result.data.choices[0].message.content)
                    }
                    return converter.toMessageEntry(result.data.choices[0].message)
                } else {
                    return converter.toMessageEntry(
                        Message(
                            MessageType.Error.toString().lowercase(), errorMessage
                        )
                    )
                }
            }

            is Resource.Error -> {
                return MessageEntry(MessageType.Error, result.message!!)
            }

            is Resource.Loading -> {
                return converter.toMessageEntry(
                    Message(
                        MessageType.Error.toString().lowercase(), result.message.toString()
                    )
                )
            }

            else -> {
                return converter.toMessageEntry(
                    Message()
                )
            }
        }
    }

    private fun composeMessages(): List<Message> {
        val userMessage = Message(
            content = fullUserMessage, role = MessageType.User.toString().lowercase()
        )
        return listOf(systemMessage, assistantMessage, userMessage)
    }

    private fun extractFullDescription(assistantMessage: String): String =
        if (!assistantMessage.contains(specificIsMarker)) assistantMessage.substringAfter(
            markerPhrase
        ) else assistantMessage.substringAfter(
            deviantMarkerPhrase
        )

    private fun extractQuestionObj(assistantMessage: String): String? {
        if (assistantMessage.startsWith(questionWords[0])) {
            return assistantMessage.substringAfter(delimiter = "${questionWords[0]} ")
                .substringBefore(' ')
        }
        if (assistantMessage.startsWith(questionWords[1])) {
            return "number of ${
                assistantMessage.substringAfter(delimiter = " ${questionWords[1]} ").split(' ')
                    .first()
            }"
        }
        return " additionally: "
    }

}