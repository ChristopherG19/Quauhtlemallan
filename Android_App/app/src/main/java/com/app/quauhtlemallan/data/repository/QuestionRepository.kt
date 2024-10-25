package com.app.quauhtlemallan.data.repository

import android.util.Log
import com.app.quauhtlemallan.data.model.Question
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class QuestionRepository {
    private val database = Firebase.database

    suspend fun getQuestions(): List<Question> {
        val ref = database.getReference("preguntas/groups")
        return try {
            val snapshot = ref.orderByChild("type").equalTo("tof").get().await()
            val questionsList = mutableListOf<Question>()
            for (groupSnapshot in snapshot.children) {
                val questionsSnapshot = groupSnapshot.child("questions")
                if (questionsSnapshot.exists()) {
                    val questions = questionsSnapshot.children.mapNotNull { it.getValue(Question::class.java) }
                    questionsList.addAll(questions)
                }
            }
            Log.d("QuestionRepository", "Preguntas obtenidas: ${questionsList.size}")
            for (question in questionsList) {
                Log.d("QuestionRepository", "Pregunta: ${question.pregunta}")
            }

            questionsList

        } catch (e: Exception) {
            Log.e("QuestionRepository", "Error obteniendo preguntas: ${e.message}")
            emptyList()
        }
    }

}