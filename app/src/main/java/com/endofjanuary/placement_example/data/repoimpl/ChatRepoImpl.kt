package com.endofjanuary.placement_example.data.repoimpl

import com.endofjanuary.placement_example.data.remote.gpt.ChatCompletionApi
import com.endofjanuary.placement_example.data.remote.gpt.request.Post
import com.endofjanuary.placement_example.data.remote.gpt.response.CompletionGptObj
import com.endofjanuary.placement_example.domain.repo.ChatRepo
import com.endofjanuary.placement_example.utils.Resource


class ChatRepoImpl(
    private val api: ChatCompletionApi
) : ChatRepo {
    override suspend fun postToGpt(body: Post): Resource<CompletionGptObj> {
        val response = try {
            api.postToChat(body)
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        return Resource.Success(response)
    }
}