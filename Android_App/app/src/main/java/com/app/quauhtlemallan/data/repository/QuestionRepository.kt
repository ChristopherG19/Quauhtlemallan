package com.app.quauhtlemallan.data.repository

import android.util.Log
import com.app.quauhtlemallan.data.model.Question
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class QuestionRepository {
    private val database = Firebase.database

    suspend fun getQuestionsToF(): List<Question> {
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
            questionsList.shuffled().take(10)
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Error obteniendo preguntas: ${e.message}")
            emptyList()
        }
    }

    suspend fun getQuestionsTime(): List<Question> {
        val ref = database.getReference("preguntas/groups")
        return try {
            val snapshot = ref.orderByChild("type").equalTo("time").get().await()
            val questionsList = mutableListOf<Question>()
            for (groupSnapshot in snapshot.children) {
                val questionsSnapshot = groupSnapshot.child("questions")
                if (questionsSnapshot.exists()) {
                    val questions = questionsSnapshot.children.mapNotNull { it.getValue(Question::class.java) }
                    questionsList.addAll(questions)
                }
            }
            questionsList.shuffled().take(10)
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Error obteniendo preguntas: ${e.message}")
            emptyList()
        }
    }

    suspend fun getQuestionsByCategory(category: String): List<Question> {
        val ref = database.getReference("preguntas/groups")
        return try {
            val snapshot = ref.orderByChild("type").equalTo("ctg").get().await()
            val questionsList = mutableListOf<Question>()

            for (groupSnapshot in snapshot.children) {
                val categoriesSnapshot = groupSnapshot.child("categories")
                if (categoriesSnapshot.exists()) {
                    for (categorySnapshot in categoriesSnapshot.children) {
                        val categoryName = categorySnapshot.child("category").getValue(String::class.java)
                        if (categoryName == category) {
                            val questionsSnapshot = categorySnapshot.child("questions")
                            val questions = questionsSnapshot.children.mapNotNull { it.getValue(Question::class.java) }
                            questionsList.addAll(questions)
                        }
                    }
                }
            }
            questionsList.shuffled().take(10)
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Error obteniendo preguntas: ${e.message}")
            emptyList()
        }
    }
}