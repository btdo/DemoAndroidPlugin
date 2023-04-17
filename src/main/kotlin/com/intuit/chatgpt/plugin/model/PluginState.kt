package com.intuit.chatgpt.plugin.model

data class PluginState(
    var url: String = "https://api.openai.com/v1/chat/completions/",
    var apiKey: String = "",
    var model: String = "gpt-3.5-turbo"
)