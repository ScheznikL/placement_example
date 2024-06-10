package com.endofjanuary.placement_example.data.remote.gpt

import com.endofjanuary.placement_example.OPENAI_API_KEY
import okhttp3.Interceptor
import okhttp3.Response

const val HEADER_NAME = "Authorization"
const val TOKEN_TYPE = "Bearer "

class AuthTokenGptInterseptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = OPENAI_API_KEY
        if (token.isNotBlank()) {
            request = request.newBuilder()
                .addHeader(HEADER_NAME, TOKEN_TYPE + token).build()
        }
        return chain.proceed(request)
    }
}