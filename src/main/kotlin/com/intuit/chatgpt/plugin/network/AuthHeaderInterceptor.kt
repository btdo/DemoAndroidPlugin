package com.intuit.chatgpt.plugin.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Inject authorization headers into request
 *
 */
class AuthHeaderInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val original = chain.request()
        val newRequestBuilder = original.newBuilder()
        newRequestBuilder.addHeader("Authorization", "Bearer $apiKey")
        val request = newRequestBuilder.build()
        return@runBlocking chain.proceed(request)
    }
}
