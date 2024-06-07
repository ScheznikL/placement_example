package com.endofjanuary.placement_example.data.remote.gpt

import com.endofjanuary.placement_example.data.remote.gpt.request.Post
import com.endofjanuary.placement_example.data.remote.gpt.response.CompletionGptObj
import retrofit2.http.Body
import retrofit2.http.POST

private const val COMPLETION_END_POINT = "chat/completions"

interface ChatComplitionApi {
    @POST(COMPLETION_END_POINT)
    suspend fun postToChat(
        @Body body: Post,
    ): CompletionGptObj
}