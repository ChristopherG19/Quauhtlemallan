package com.app.quauhtlemallan.data.repository

import android.net.Uri
import android.util.Log
import com.app.quauhtlemallan.data.model.AchievementData
import com.app.quauhtlemallan.data.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) {

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun isEmailProvider(): Boolean {
        return firebaseAuth.currentUser?.providerData?.any { it.providerId == EmailAuthProvider.PROVIDER_ID } ?: false
    }

    suspend fun getUserProfile(userId: String): User? {
        return try {
            val snapshot = database.getReference("usuarios").child(userId).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createUserProfile(user: User): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false
            database.getReference("usuarios").child(userId).setValue(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserProfile(username: String, country: String, profileImage: String): Boolean {
        return try {
            val updates = mapOf(
                "username" to username,
                "country" to country,
                "profileImage" to profileImage
            )

            Log.d("UserRepository", "Updating profile with: $updates")  // Añadir log para ver qué datos se están enviando

            getCurrentUserId()?.let { database.getReference("usuarios").child(it).updateChildren(updates).await() }

            Log.d("UserRepository", "Profile updated successfully")  // Confirmación de que el perfil se actualizó

            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating profile: ${e.message}", e)  // Añadir log para ver el error
            false
        }
    }

    suspend fun updatePassword(newPassword: String) {
        firebaseAuth.currentUser?.updatePassword(newPassword)?.await()
    }

    suspend fun uploadProfileImage(imageUri: Uri): String? {
        val userId = getCurrentUserId() ?: return null
        return try {
            val fileRef = storage.reference.child("UserImages/${userId}_profile_image")
            fileRef.putFile(imageUri).await()
            fileRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsersOrderedByScore(): List<User> {
        return try {
            val snapshot = database.getReference("usuarios")
                .orderByChild("score")
                .get()
                .await()

            val userList = mutableListOf<User>()
            for (data in snapshot.children) {
                val user = data.getValue(User::class.java)
                user?.let { userList.add(it) }
            }

            userList.sortedByDescending { it.score }
        } catch (e: Exception) {
            e.message?.let { Log.e("Error_GetUsersList", it) }
            emptyList()
        }
    }

    suspend fun getAllBadges(): List<AchievementData> {
        val badges = mutableListOf<AchievementData>()
        try {
            val snapshot = database.getReference("insignias").get().await()
            for (categorySnapshot in snapshot.children) {
                for (badgeSnapshot in categorySnapshot.children) {
                    val badge = badgeSnapshot.getValue(AchievementData::class.java)
                    badge?.let {
                        badges.add(it)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("getAllBadges", "Error: ${e.message}")
        }
        return badges
    }

    suspend fun getBadgesByCategory(categoryId: String): List<AchievementData> {
        val badges = mutableListOf<AchievementData>()
        try {
            val snapshot = database.getReference("insignias/$categoryId").get().await()
            for (badgeSnapshot in snapshot.children) {
                val badge = badgeSnapshot.getValue(AchievementData::class.java)
                badge?.let {
                    badges.add(it)
                }
            }
        } catch (e: Exception) {
            Log.e("getBadgesByCategory", "Error: ${e.message}")
        }
        return badges
    }

    suspend fun getUserDiscoveryPercentage(): Int {
        return try {
            val userId = getCurrentUserId() ?: return 0

            database.getReference("usuarios/$userId/porcentajeDescubierto").get().await()
                .getValue(Int::class.java) ?: 0
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting discovery percentage: ${e.message}", e)
            0
        }
    }

    suspend fun getUserTotalScore(): Int {
        return try {
            val userId = getCurrentUserId() ?: return 0
            database.getReference("usuarios/$userId/score").get().await()
                .getValue(Int::class.java) ?: 0
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting total score: ${e.message}", e)
            0
        }
    }

    suspend fun getUserBadgeProgress(badgeId: String): Int {
        return try {
            val userId = getCurrentUserId() ?: return 0

            val badgeProgressSnapshot = database.getReference("usuarios/$userId/insignias/$badgeId").get().await()
            badgeProgressSnapshot.getValue(Int::class.java) ?: 0
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting badge progress for $badgeId: ${e.message}", e)
            0
        }
    }

    suspend fun addPointsToUserScore(points: Int) {
        val userId = getCurrentUserId() ?: return
        val userRef = database.getReference("usuarios").child(userId)

        val currentScore = userRef.child("score").get().await().getValue(Int::class.java) ?: 0
        userRef.child("score").setValue(currentScore + points)
    }

    suspend fun addPointsToBadge(badgeId: String, points: Int) {
        val userId = getCurrentUserId() ?: return
        val badgeRef = database.getReference("usuarios").child(userId).child("insignias").child(badgeId)

        val currentPoints = badgeRef.get().await().getValue(Int::class.java) ?: 0
        val maxPoints = getMaxPointsForBadge(badgeId)

        val newPoints = minOf(currentPoints + points, maxPoints)
        badgeRef.setValue(newPoints)
    }

    private suspend fun getMaxPointsForBadge(badgeId: String): Int {
        return try {
            val snapshot = database.getReference("insignias").child(badgeId).child("maxPoints").get().await()
            snapshot.getValue(Int::class.java) ?: 100
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting maxPoints: ${e.message}", e)
            100
        }
    }

    suspend fun calculateUserLevel(totalScore: Int): Triple<Int, Int, Int> {
        val nivelesRef = Firebase.database.getReference("niveles")
        val nivelesSnapshot = nivelesRef.get().await()

        var currentLevel = 0
        var pointsForCurrentLevel = 0
        var pointsForNextLevel = 0

        val nivelesOrdenados = nivelesSnapshot.children.sortedBy {
            it.key?.removePrefix("nivel")?.toIntOrNull() ?: 0
        }

        for ((index, nivel) in nivelesOrdenados.withIndex()) {
            val levelPoints = nivel.value.toString().toInt()

            if (totalScore < levelPoints) {
                pointsForNextLevel = levelPoints
                break
            }

            currentLevel = index
            pointsForCurrentLevel = levelPoints
        }

        val pointsToNextLevel = if (pointsForNextLevel > 0) pointsForNextLevel - totalScore else 0
        val currentPointsWithinLevel = totalScore - pointsForCurrentLevel

        return Triple(currentLevel, pointsToNextLevel, currentPointsWithinLevel)
    }

    suspend fun getUserLevel(totalScore: Int): Int {
        val nivelesRef = Firebase.database.getReference("niveles")
        val nivelesSnapshot = nivelesRef.get().await()

        var currentLevel = 1

        for (nivel in nivelesSnapshot.children.sortedBy { it.key?.removePrefix("nivel")?.toIntOrNull() ?: 0 }) {
            val levelPoints = nivel.value.toString().toInt()
            if (totalScore < levelPoints) {
                break
            }
            currentLevel = nivel.key?.removePrefix("nivel")?.toInt() ?: currentLevel
        }

        return currentLevel
    }


}