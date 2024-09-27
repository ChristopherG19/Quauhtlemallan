package com.app.quauhtlemallan.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.ChatModelRequest
import com.app.quauhtlemallan.data.ChatModelResponse
import com.app.quauhtlemallan.service.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChatBotFragment : Fragment() {

    private lateinit var responseTextview: TextView
    private lateinit var emptyTextview: TextView
    private lateinit var inputEditText: TextInputEditText
    private lateinit var sendBtn: ImageView
    private lateinit var apiService: ApiService
    private lateinit var progressDialog: ProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Kukul"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_chat_bot, container, false)

        // Inicializar vistas
        inputEditText = view.findViewById(R.id.inputEditText)
        emptyTextview = view.findViewById(R.id.emptyTv)
        responseTextview = view.findViewById(R.id.responseTv)
        sendBtn = view.findViewById(R.id.sendBtn)

        // Inicializar ProgressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        // Configurar el botón de enviar
        sendBtn.setOnClickListener {
            if (inputEditText.text.toString().isNotEmpty()) {
                progressDialog.show()
                makeApiCall()
            }
        }

        return view
    }

    // Método para realizar la llamada a la API
    private fun makeApiCall() {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/") // Change ip
            .client(okHttpClient)
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
                } else {
                    Log.e("API Error", "Failed.....")
                }
            }

            override fun onFailure(call: Call<ChatModelResponse>, t: Throwable) {
                progressDialog.dismiss()
                inputEditText.text?.clear()
                Log.e("API Error", "Failed....: ${t.message}")
            }
        })
    }
}
