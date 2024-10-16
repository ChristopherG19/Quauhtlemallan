package com.app.quauhtlemallan.data.api

import com.app.quauhtlemallan.data.model.ChatModelRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("llm/generate_text")
    fun createPost(@Body post: ChatModelRequest): Call<ResponseBody>
}
