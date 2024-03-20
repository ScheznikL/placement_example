package com.endofjanuary.placement_example.chat

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.data.converters.MessageToUIConverter
import com.endofjanuary.placement_example.data.converters.MessageType
import com.endofjanuary.placement_example.data.models.MessageEntry
import com.endofjanuary.placement_example.data.remote.gpt.request.Post
import com.endofjanuary.placement_example.data.remote.gpt.response.Message
import com.endofjanuary.placement_example.repo.ChatRepo
import com.endofjanuary.placement_example.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val systemMessage: Message = Message(
    "Your role is to present to user at the right moment a description of the most accurate {object} model" +
            "and start that message with word 'FINAL object is'. Description will be based on user input. " +
            "Important: if user input contains word END just give FINAL response, no need ask more questions. " +
            "Or if user input contains word NEXT keep asking questions.", "system"
)
val assistantMessage: Message = Message(
    "You need to ask questions specific to user {object} (here object needs to be replaced with real user one) like:" +
            "'What color should the {object} be?''What size should the {object} be?''What shape should be the {object}?'" +
            "'What material should the {object} be made of?'. " +
            "If there is a need of next questions provide them with some compatible examples from brackets: " +
            "'What style (e.g. fantasy, cartoon, sci-fi or futurist, realistic, ancient, beautiful, " +
            "elegant, ultra realistic, trending on artstation, masterpiece, cinema 4d, unreal engine, octane render)?'" +
            "'What quality the object needs to be or number of details (e.g. highly detailed, high resolution, highest quality, best quality, 4K, 8K, HDR, studio quality)'",
    "assistant"
)

class ChatScreenViewModel(
    private val chatRepository: ChatRepo,
    private val modelRoomRepo: ModelsRepo
) : ViewModel() {

    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    val isSuccess = mutableStateOf(false)
    var inputValueState = mutableStateOf("")

    private val _messagesListState = mutableStateOf<List<MessageEntry>>(listOf())
    val messagesListState: State<List<MessageEntry>> get() = _messagesListState


    var description: String? = null

    private var fullUserMessage = ""
    var selectedUri = mutableStateOf(Uri.EMPTY)

    val modelId = mutableStateOf(0)

    fun send(userMessageContext: String) {
        val converter = MessageToUIConverter()
        description = userMessageContext

        isLoading.value = true
        loadError.value = ""
        _messagesListState.value =
            _messagesListState.value.plus(
                converter.toMessageEntry(
                    Message(
                        role = "user",
                        content = userMessageContext
                    )
                )
            )
        // if (fullUserMessage.isEmpty())
        fullUserMessage += "$userMessageContext "

        Log.d("sendReq", fullUserMessage)

        viewModelScope.launch {
            val result =
                chatRepository.postToGpt(Post(messages = composeMessages(userMessageContext)))
            when (result) {
                is Resource.Success -> {
                    isLoading.value = false
                    if (result.data != null) {
                        _messagesListState.value =
                            _messagesListState.value.plus(converter.toMessageEntry(result.data.choices[0].message))
                        if (!result.data.choices[0].message.content.contains("FINAL object is")) {
                            fullUserMessage += "${extractQuestionObj(result.data.choices[0].message.content)}: "
                        } else {
                            description =
                                extractFullDescription(result.data.choices[0].message.content)
                        }
                    }
                }

                is Resource.Error -> {
                    isLoading.value = false
                    //return Resource.Error(result.message!!)
                    loadError.value = result.message!!
                }

                is Resource.Loading -> {
                    isLoading.value = true
                }

                else -> {
                    isLoading.value = true
                }
            }
        }

    }

    private fun extractFullDescription(assistantMessage: String): String =
        if (!assistantMessage.contains(" is : ")) assistantMessage.substringAfter("FINAL object is") else assistantMessage.substringAfter(
            "FINAL object is : "
        )


    private fun extractQuestionObj(assistantMessage: String): String? {
        if (assistantMessage.startsWith("What")) {
            return assistantMessage.substringAfter(delimiter = "What ").substringBefore(' ')
        }
        if (assistantMessage.startsWith("How many")) {
            return "number of ${
                assistantMessage.substringAfter(delimiter = " How many ").split(' ').first()
            }"
        }
        return " additionally: "
    }

    private fun composeMessages(newMessage: String): List<Message> { //todo remove
        val userMessage = Message(
            //  content = if (fullUserMessage.isNotEmpty()) "$fullUserMessage $newMessage," else newMessage,
            content = fullUserMessage,
            role = "user"
        )
        return listOf(systemMessage, assistantMessage, userMessage)
    }

    fun onPhotoPickerSelect(uri: Uri?) {
        if (uri != null) selectedUri.value = uri
    }

    fun getId() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val lastModelStat = modelRoomRepo.getLastModel()) {
                is Resource.Error -> modelId.value = -1
                is Resource.Loading -> modelId.value = -2
                is Resource.None -> {}
                is Resource.Success -> {
                    Log.d("modelID", modelId.value.toString())
                    modelId.value = lastModelStat.data!!.id + 1
                }
            }
        }

    }

    fun loadingModel() {
        _messagesListState.value = _messagesListState.value.plus(
            MessageEntry(
                MessageType.Loading,
                ""
            )
        )
    }
}