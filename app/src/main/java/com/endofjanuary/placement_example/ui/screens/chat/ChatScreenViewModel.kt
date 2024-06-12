package com.endofjanuary.placement_example.ui.screens.chat

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.data.remote.gpt.request.Post
import com.endofjanuary.placement_example.data.remote.gpt.response.Message
import com.endofjanuary.placement_example.domain.converters.MessageToUIConverter
import com.endofjanuary.placement_example.domain.converters.MessageType
import com.endofjanuary.placement_example.domain.models.MessageEntry
import com.endofjanuary.placement_example.domain.repo.ChatRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.launch

val systemMessage: Message = Message(
    "Your role is to present to user at the right moment a description of the most accurate {object} model" + "and start that message with word 'FINAL object is'. Description will be based on user input. " + "Important: if user input contains word END just give FINAL response, no need ask more questions. " + "Or if user input contains word NEXT keep asking questions.",
    "system"
)
val assistantMessage: Message = Message(
    "You need to ask questions specific to user {object} (here object needs to be replaced with real user one) like:" + "'What color should the {object} be?''What size should the {object} be?''What shape should be the {object}?'" + "'What material should the {object} be made of?'. " + "If there is a need of next questions provide them with some compatible examples from brackets: " + "'What style (e.g. fantasy, cartoon, sci-fi or futurist, realistic, ancient, beautiful, " + "elegant, ultra realistic, trending on artstation, masterpiece, cinema 4d, unreal engine, octane render)?'" + "'What quality the object needs to be or number of details (e.g. highly detailed, high resolution, highest quality, best quality, 4K, 8K, HDR, studio quality)'",
    "assistant"
)

const val markerPhrase: String = "FINAL object is"
const val deviantMarkerPhrase: String = "FINAL object is : "
const val specificIsMarker: String = " is : "

val questionWords = arrayOf( "What","How many")
class ChatScreenViewModel(
    private val chatRepository: ChatRepo
) : ViewModel() {

    val isAutoRefineEnabled = mutableStateOf(false)


    var loadError = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    var inputValueState = mutableStateOf("")
    private val _messagesListState = mutableStateOf<List<MessageEntry>>(listOf())

    val messagesListState: State<List<MessageEntry>> get() = _messagesListState

    var description: String? = null
    private var fullUserMessage = ""

    var selectedUri = mutableStateOf(Uri.EMPTY)
    val modelId = mutableStateOf(0)
    private val converter = MessageToUIConverter()
    fun send(userMessageContext: String) {
        loadError.value = null
        isLoading.value = true

        description = userMessageContext

        _messagesListState.value = _messagesListState.value.plus(
            converter.toMessageEntry(
                Message(
                    role = MessageType.User.toString().lowercase(), content = userMessageContext
                )
            )
        )
        fullUserMessage += "$userMessageContext "
        viewModelScope.launch {
            val result =
                chatRepository.postToGpt(Post(messages = composeMessages()))
            when (result) {
                is Resource.Success -> {
                    isLoading.value = false
                    if (result.data != null) {
                        _messagesListState.value =
                            _messagesListState.value.plus(converter.toMessageEntry(result.data.choices[0].message))
                        if (!result.data.choices[0].message.content.contains(markerPhrase)) {
                            fullUserMessage += "${extractQuestionObj(result.data.choices[0].message.content)}: "
                        } else {
                            description =
                                extractFullDescription(result.data.choices[0].message.content)
                        }
                    }
                }

                is Resource.Error -> {
                    isLoading.value = false
                    loadError.value = result.message!!
                }

                is Resource.Loading -> {
                    isLoading.value = true
                    loadError.value = null
                }

                else -> {
                    isLoading.value = true
                    loadError.value = null
                }
            }
        }
    }

    fun addAutoRefineMessage(context: Context) {
        _messagesListState.value += converter.toMessageEntry(
            Message(
                role = MessageType.AutoRefine.toString().lowercase(),
                content = context.getString(R.string.refine_mode_message)
            )
        )
    }

    private fun extractFullDescription(assistantMessage: String): String =
        if (!assistantMessage.contains(specificIsMarker)) assistantMessage.substringAfter(
            markerPhrase
        ) else assistantMessage.substringAfter(
            deviantMarkerPhrase
        )

    private fun extractQuestionObj(assistantMessage: String): String? {
        if (assistantMessage.startsWith(questionWords[0])) {
            return assistantMessage.substringAfter(delimiter = "${questionWords[0]} ").substringBefore(' ')
        }
        if (assistantMessage.startsWith(questionWords[1])) {
            return "number of ${
                assistantMessage.substringAfter(delimiter = " ${questionWords[1]} ").split(' ').first()
            }"
        }
        return " additionally: "
    }

    private fun composeMessages(): List<Message> {
        val userMessage = Message(
            content = fullUserMessage, role = MessageType.User.toString().lowercase()
        )
        return listOf(systemMessage, assistantMessage, userMessage)
    }

    fun loadingModel() {
        _messagesListState.value = _messagesListState.value.plus(
            MessageEntry(
                MessageType.Loading, ""
            )
        )
    }
}