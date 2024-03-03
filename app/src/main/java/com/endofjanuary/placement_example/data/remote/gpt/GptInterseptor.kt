package com.endofjanuary.placement_example.data.remote.gpt

import OPENAI_API_KEY
import okhttp3.Interceptor
import okhttp3.Response
class AuthTokenGptInterseptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = OPENAI_API_KEY
        if (token.isNotBlank()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }
}