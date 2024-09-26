package com.app.quauhtlemallan.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.ChatModelRequest
import com.app.quauhtlemallan.data.ChatModelResponse
import com.app.quauhtlemallan.service.ApiService
import com.google.android.material.textfield.TextInputEditText
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChatBotActivity : AppCompatActivity() {

    private lateinit var responseTextview: TextView
    private lateinit var emptyTextview: TextView
    private lateinit var inputEditText: TextInputEditText
    private lateinit var sendBtn: ImageView
    private lateinit var apiService: ApiService
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        inputEditText = findViewById(R.id.inputEditText)
        emptyTextview = findViewById(R.id.emptyTv)
        responseTextview = findViewById(R.id.responseTv)
        sendBtn = findViewById(R.id.sendBtn)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        sendBtn.setOnClickListener {
            if (inputEditText.text.toString().isNotEmpty()) {
                progressDialog.show()
                makeApiCall()
            }
        }
    }

    private fun makeApiCall() {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES) // Set connection timeout
            .readTimeout(10, TimeUnit.MINUTES) // Set read timeout
            .writeTimeout(10, TimeUnit.MINUTES) // Set write timeout
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .client(okHttpClient) // Set custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        val post = ChatModelRequest(200, inputEditText.text.toString(), "Researchers")
        apiService.createPost(post).enqueue(object : Callback<ChatModelResponse> {
            override fun onResponse(
                call: Call<ChatModelResponse>,
                response: Response<ChatModelResponse>
            ) {
                progressDialog.dismiss()
                inputEditText.text?.clear()
                if (response.isSuccessful) {
                    val createdPost = response.body()
                    createdPost?.let {
                        responseTextview.text = it.response
                    }
                    print("response is:::::" + createdPost)
                } else {
                    Log.e("API Error", "Failed.....")
                    print("failed  response is:::::" + response.body())
                }
            }

            override fun onFailure(call: Call<ChatModelResponse>, t: Throwable) {
                progressDialog.dismiss()
                inputEditText.text?.clear()
                Log.e("API Error", "Failed....: ${t.message}")
                print("failed response is:::::" + t.message)
            }
        })
    }
}