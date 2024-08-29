package com.endofjanuary.placement_example.data.repoimpl

import com.endofjanuary.placement_example.data.remote.gpt.ChatCompletionApi
import com.endofjanuary.placement_example.data.remote.gpt.request.Post
import com.endofjanuary.placement_example.data.remote.gpt.response.Choice
import com.endofjanuary.placement_example.data.remote.gpt.response.CompletionGptObj
import com.endofjanuary.placement_example.data.remote.gpt.response.Message
import com.endofjanuary.placement_example.data.remote.gpt.response.Usage
import com.endofjanuary.placement_example.domain.repo.ChatRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.delay


class ChatRepoImpl(
    private val api: ChatCompletionApi,
    private val serv: ApiService,
) : ChatRepo {
    override suspend fun postToGpt(body: Post): Resource<CompletionGptObj> {
        val response = try {
            api.postToChat(body)
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }

    override suspend fun testPostToGpt(body: Post): Resource<CompletionGptObj> {
        val response = try {
            serv.postToChat(body)
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }

}

class ApiService {
    private var index = 0

    val column = listOf(
        "What color should the ancient column be?",
        "What size should the ancient column be?",
        "What material should the ancient column be made of?",
        "What style should the ancient column be? (e.g., Doric, Ionic, Corinthian)",
        "What quality or level of detail should the ancient column be? (e.g., highly detailed, high resolution, best quality, 4K, 8K, HDR)",
        "FINAL object is an ancient corinthian column model. The column is large and crafted from marble, featuring a white color. It exhibits a highly detailed and high-resolution design, with intricate acanthus leaf carvings on the capital, fluted shaft, and a sturdy base. This model captures the elegance and complexity of Corinthian architecture, suitable for high-quality renderings and historical reconstructions."
    )

    val chair = listOf(
        Message(
            "What color should the chair be?",
            role = "assistant"
        ),
        Message(
            "What size should the chair be?",
            role = "assistant"
        ),
        Message(
            "What shape should the chair be?",
            role = "assistant"
        ),
        Message(
            "What material should the chair be made of?",
            role = "assistant"
        ),
        Message(
            "FINAL chair is an antique and elegant chair made of wood.",
            role = "assistant"
        ),
    )

    val otter = listOf(
        "What color should the otter be?",
        "FINAL object is a sleek, small baby otter with brown fur, designed in a realistic style. It is highly detailed and high resolution."
    )

    suspend fun postToChat(body: Post): CompletionGptObj {
        // Simulated delay to mimic real API request
        delay(100) // 1 second delay

        val test = CompletionGptObj(
            choices = listOf(
                Choice(
                    finish_reason = "complete",
                    index = 0,
                    logprobs = mapOf(
                        "token1" to -2.345,
                        "token2" to -3.456
                    ), // Example logprobs, could be any structure
                    message = /* chair[index]*/Message(
                        content = otter[index],
                        role = "assistant"
                    )
                ),
            ),
            created = 1623589200, // Example timestamp
            id = "abc123xyz456",
            model = "gpt-4",
            `object` = "text_completion",
            system_fingerprint = "fingerprint-789",
            usage = Usage(
                completion_tokens = 100,
                prompt_tokens = 20,
                total_tokens = 120
            )
        )
        index++
        return test

    }
}