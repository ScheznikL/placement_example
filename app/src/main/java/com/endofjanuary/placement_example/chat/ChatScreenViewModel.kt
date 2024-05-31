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


        description = userMessageContext

        isLoading.value = true
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
                        if (!result.data.choices[0].message.content!!.contains("FINAL object is")) {
                            fullUserMessage += "${extractQuestionObj(result.data.choices[0].message.content!!)}: "
                        } else {
                            description =
                                extractFullDescription(result.data.choices[0].message.content!!)
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
                    loadError.value = null
                }

                else -> {
                    isLoading.value = true
                    loadError.value = null
                }
            }
        }

    }

    fun addAutoRefineMessage() {
        _messagesListState.value += converter.toMessageEntry(
            Message(role = "refine", content = "You have enabled auto refine mode, so choose desired quality:")
        )
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

    fun getId() { // TODO Rethink - Why 2 ID was bad - one set by primary key and we need it to navigate (?) and we don't know it till we ask

        viewModelScope.launch(Dispatchers.IO) {
            val lastModelStat = modelRoomRepo.getLastModel()
            when (lastModelStat) {
                is Resource.Success -> {
                    modelId.value =
                        lastModelStat.data!!.id // if use update when successfully loaded no need in +1
                    Log.d("modelID", modelId.value.toString())
                }

                else -> {
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