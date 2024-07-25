package com.endofjanuary.placement_example.ui.screens.chat

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.data.remote.gpt.response.Message
import com.endofjanuary.placement_example.domain.converters.MessageType
import com.endofjanuary.placement_example.domain.models.MessageEntry
import com.endofjanuary.placement_example.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.launch

val systemMessage: Message = Message(
    "Your role is to present to user at the right moment a description of the most accurate {object} model" + "and start that message with word 'FINAL object is'. Description will be based on user input. Description must be less or equal to 80 words" + "Important: if user input contains word END just give FINAL response, no need ask more questions. " + "Or if user input contains word NEXT keep asking questions.",
    "system"
)
val assistantMessage: Message = Message(
    "You need to ask questions specific to user {object} (here object needs to be replaced with real user one) like:" + "'What color should the {object} be?''What size should the {object} be?''What shape should be the {object}?'" + "'What material should the {object} be made of?'. " + "If there is a need of next questions provide them with some compatible examples from brackets: " + "'What style (e.g. fantasy, cartoon, sci-fi or futurist, realistic, ancient, beautiful, " + "elegant, ultra realistic, trending on artstation, masterpiece, cinema 4d, unreal engine, octane render)?'" + "'What quality the object needs to be or number of details (e.g. highly detailed, high resolution, highest quality, best quality, 4K, 8K, HDR, studio quality)'",
    "assistant"
)

const val markerPhrase: String = "FINAL object is"
const val deviantMarkerPhrase: String = "FINAL object is : "
const val specificIsMarker: String = " is : "

val questionWords = arrayOf("What", "How many")

class ChatScreenViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        Log.d("cleared","ChatVM")
    }
    val isAutoRefineEnabled = mutableStateOf(false)

    var inputValueState = mutableStateOf("")
    private val _messagesListState = mutableStateOf<List<MessageEntry>>(listOf())

    val messagesListState: State<List<MessageEntry>> get() = _messagesListState

    var description: String? = null
    private var fullUserMessage = ""

    val modelId = mutableStateOf(0)
    fun send(userMessageContext: String) {
        _messagesListState.value = _messagesListState.value.plus(
            MessageEntry(
                messageType = MessageType.User, content = userMessageContext
            )
        )
        viewModelScope.launch {
            val result = sendMessageUseCase.send(userMessageContext)
            _messagesListState.value = _messagesListState.value.plus(result)
            description = result.content.removePrefix(markerPhrase)
        }
    }

    fun addAutoRefineMessage(context: Context) {
        _messagesListState.value +=
            MessageEntry(
                messageType = MessageType.AutoRefine,
                content = context.getString(R.string.refine_mode_message)
            )
    }

    fun loadingModel() {
        Log.d("Loading M ", "loadingModel fun")
        _messagesListState.value = _messagesListState.value.plus(
            MessageEntry(
                MessageType.Loading, ""
            )
        )
    }

    fun cancelLoading(){
        Log.d("Loading M ", "cancel Loading fun")

        _messagesListState.value = _messagesListState.value.minus(
            MessageEntry(
                MessageType.Loading, ""
            )
        )
    }
}