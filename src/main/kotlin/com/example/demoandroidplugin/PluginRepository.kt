package com.example.demoandroidplugin

import ChatGptRequest
import com.example.demoandroidplugin.model.ChatGptResponse

interface PluginRepository {
    suspend fun query(query: String): ChatGptResponse
}
class PluginRepositoryImpl (private val api: ChatGptApi) : PluginRepository {
    override suspend fun query(query: String): ChatGptResponse {
         return api.queryChatGpt(ChatGptRequest(messages = listOf(ChatGptRequest.Message(content = "blah"))))
        //return ChatGptResponse("Hello from Repository")
    }
}