package com.personal.lifeos.ai

import com.google.gson.annotations.SerializedName

// Retrofit Data Models
data class OpenAIChatRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<OpenAIMessage>,
    val response_format: ResponseFormat? = null,
    val temperature: Double = 0.0
)

data class OpenAIMessage(
    val role: String,
    val content: String
)

data class ResponseFormat(
    val type: String = "json_object"
)

data class OpenAIChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: OpenAIMessage
)
