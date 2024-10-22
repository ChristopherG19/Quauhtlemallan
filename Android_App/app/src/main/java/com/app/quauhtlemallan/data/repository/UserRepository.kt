package com.app.quauhtlemallan.data.repository

import android.net.Uri
import android.util.Log
import com.app.quauhtlemallan.data.model.AchievementData
import com.app.quauhtlemallan.data.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) {

    // Obtén el ID del usuario actual si está autenticado
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    // Verifica si el proveedor de autenticación es correo y contraseña
    fun isEmailProvider(): Boolean {
        return firebaseAuth.currentUser?.providerData?.any { it.providerId == EmailAuthProvider.PROVIDER_ID } ?: false
    }

    // Obtiene el perfil del usuario desde Firebase Realtime Database
    suspend fun getUserProfile(userId: String): User? {
        return try {
            val snapshot = database.getReference("usuarios").child(userId).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Crea un nuevo perfil de usuario en Firebase Realtime Database
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

}