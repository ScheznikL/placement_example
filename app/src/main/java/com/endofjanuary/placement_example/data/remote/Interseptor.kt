package com.endofjanuary.placement_example.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = "msy_mTSwQvvjyVVw1LnJbH3vZLrPkZahSxajpC34"
        if (!token.isNullOrBlank()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }
}