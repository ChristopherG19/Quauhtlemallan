package com.app.quauhtlemallan.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.quauhtlemallan.data.api.ApiService
import com.app.quauhtlemallan.data.model.ChatModelRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChatViewModel() : ViewModel() {
    private val _chatResponse = mutableStateOf<String>("")
    val chatResponse: State<String> = _chatResponse

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val apiService: ApiService

    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://chatbot-27zqhyysfq-uc.a.run.app/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun sendChatMessage(inputText: String) {
        _loading.value = true

        var numToken = 9

        numToken = when {
            inputText.contains("capital", ignoreCase = true) || inputText.contains("cabecera", ignoreCase = true) -> {
                9
            }

            inputText.contains("fecha", ignoreCase = true) || inputText.contains("información", ignoreCase = true) -> {
                50
            }

            inputText.contains("historia", ignoreCase = true) || inputText.contains("origen", ignoreCase = true) -> {
                30
            }

            else -> {
                15
            }
        }

        val post = ChatModelRequest(numToken, inputText)

        apiService.createPost(post).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    response.body()?.let {
                        val responseString = it.string()
                        val gson = Gson()
                        val jsonResponse = gson.fromJson(responseString, JsonObject::class.java)
                        val decodedResponse = jsonResponse.get("response").asString
                        Log.e("RESPONSE", decodedResponse)
                        val cleanResponse = decodedResponse.substringAfter("Respuesta:").trim()

                        _chatResponse.value = cleanResponse

                    }
                } else {
                    Log.e("OnResponse", "Fallo")
                    _chatResponse.value = "Error: Response not successful"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _loading.value = false
                _chatResponse.value = "Error: ${t.message}"
            }
        })
    }
}
