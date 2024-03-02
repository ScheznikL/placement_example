package com.endofjanuary.placement_example.data.remote.gpt

import okhttp3.Interceptor
import okhttp3.Response
class AuthTokenGptInterseptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = "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"
        if (!token.isNullOrBlank()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }
}