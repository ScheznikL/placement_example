package com.endofjanuary.placement_example.domain.repo

import com.endofjanuary.placement_example.data.remote.gpt.request.Post
import com.endofjanuary.placement_example.data.remote.gpt.response.CompletionGptObj
import com.endofjanuary.placement_example.utils.Resource

interface ChatRepo {
    suspend fun postToGpt(body : Post) : Resource<CompletionGptObj>
}