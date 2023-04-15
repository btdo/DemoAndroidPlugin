package com.intuit.chatgpt.plugin.model

data class PluginState(
    var gptUrl: String = "https://api.openai.com/v1/chat/completions/",
    var apiKey: String = "api key",
    var model: String = "gpt-3.5-turbo"
)