package com.intuit.chatgpt.plugin.network

import ChatGptRequest
import com.intuit.chatgpt.plugin.model.ChatGptResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatGptApi {
    @POST("/v1/chat/completions")
    suspend fun queryChatGpt(@Body request: ChatGptRequest): ChatGptResponse
}