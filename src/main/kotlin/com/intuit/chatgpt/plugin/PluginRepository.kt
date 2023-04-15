package com.intuit.chatgpt.plugin

import ChatGptRequest
import com.intuit.chatgpt.plugin.model.ChatGptResponse
import com.intuit.chatgpt.plugin.network.ChatGptApi

interface PluginRepository {
    suspend fun query(query: String): ChatGptResponse
}
class PluginRepositoryImpl (private val api: ChatGptApi) : PluginRepository {
    override suspend fun query(query: String): ChatGptResponse {
         return api.queryChatGpt(ChatGptRequest(messages = listOf(ChatGptRequest.Message(content = "blah"))))
        //return ChatGptResponse("Hello from Repository")
    }
}