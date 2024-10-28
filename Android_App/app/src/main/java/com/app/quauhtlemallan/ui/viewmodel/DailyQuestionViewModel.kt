package com.app.quauhtlemallan.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.quauhtlemallan.data.model.Question
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

class DailyQuestionViewModel : ViewModel() {

    var dailyQuestion: MutableState<Question?> = mutableStateOf(null)
        private set
    var hasAnswered: MutableState<Boolean> = mutableStateOf(false)
        private set
    var showDialog: MutableState<Boolean> = mutableStateOf(false)
        private set
    var isCorrectAnswer: MutableState<Boolean> = mutableStateOf(false)
        private set

    private val db = FirebaseDatabase.getInstance()

    fun resetDailyQuestion(context: Context) {
        val sharedPreferences = context.getSharedPreferences("dailyQuestionPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    fun loadDailyQuestion(context: Context) {
        val sharedPreferences = context.getSharedPreferences("dailyQuestionPrefs", Context.MODE_PRIVATE)
        val savedQuestion = sharedPreferences.getString("currentQuestion", null)

        val lastFetchTime = sharedPreferences.getLong("lastFetchTime", 0L)
        val hasAnsweredToday = sharedPreferences.getBoolean("hasAnsweredToday", false)
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000

        if (hasAnsweredToday) {
            hasAnswered.value = true
            return
        }

        if (savedQuestion != null) {
            val question = Gson().fromJson(savedQuestion, Question::class.java)
            dailyQuestion.value = question
        } else {
            if (currentTime - lastFetchTime >= oneDayMillis) {
                db.getReference("preguntas/groups")
                    .orderByChild("type").equalTo("daily")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        for (groupSnapshot in snapshot.children) {
                            val questionsSnapshot = groupSnapshot.child("questions")
                            if (questionsSnapshot.exists()) {
                                val questions = questionsSnapshot.children.map { it.getValue(Question::class.java) }
                                val randomQuestion = questions.randomOrNull()

                                if (randomQuestion != null) {
                                    dailyQuestion.value = randomQuestion
                                    sharedPreferences.edit().putString("currentQuestion", Gson().toJson(randomQuestion)).apply()
                                    sharedPreferences.edit().putLong("lastFetchTime", currentTime).apply()
                                }
                            }
                        }
                    }
                    .addOnFailureListener { error ->
                        Log.e("DailyQuestionViewModel", "Error cargando pregunta: ${error.message}")
                    }
            } else {
                dailyQuestion.value = null
            }
        }
    }

    fun submitAnswer(context: Context, isCorrect: Boolean) {
        val sharedPreferences = context.getSharedPreferences("dailyQuestionPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("hasAnsweredToday", true).apply()

        isCorrectAnswer.value = isCorrect

        showDialog.value = true

        sharedPreferences.edit().remove("currentQuestion").apply()
        hasAnswered.value = true
    }

    fun closeDialog() {
        showDialog.value = false
    }
}
