package com.endofjanuary.placement_example.data.remote.gpt

import com.endofjanuary.placement_example.data.remote.gpt.request.Post
import com.endofjanuary.placement_example.data.remote.gpt.response.CompletionGptObj
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatComplitionApi {
    @POST("chat/completions")
    suspend fun postToChat(
        @Body body: Post,
    ): CompletionGptObj
}