package com.app.quauhtlemallan.data

data class ChatModelResponse(
    val userId: Int,
    val response: String,
    val body: String
)

data class ChatModelRequest(
    val no_words: Int,
    val input_text: String,
    val blog_style: String
)