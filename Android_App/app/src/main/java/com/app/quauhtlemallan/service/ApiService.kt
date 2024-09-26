package com.app.quauhtlemallan.service

import com.app.quauhtlemallan.data.ChatModelRequest
import com.app.quauhtlemallan.data.ChatModelResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("llm/generate_text")
    fun createPost(@Body post: ChatModelRequest): Call<ChatModelResponse>
}