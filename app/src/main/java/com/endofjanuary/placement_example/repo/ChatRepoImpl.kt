package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.data.remote.gpt.ChatComplitionApi
import com.endofjanuary.placement_example.data.remote.gpt.request.Post
import com.endofjanuary.placement_example.data.remote.gpt.response.CompletionGptObj
import com.endofjanuary.placement_example.utils.Resource


class ChatRepoImpl(
    private val api: ChatComplitionApi
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