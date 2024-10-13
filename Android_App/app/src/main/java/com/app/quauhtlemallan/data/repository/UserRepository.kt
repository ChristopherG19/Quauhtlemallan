package com.app.quauhtlemallan.data.repository

import com.app.quauhtlemallan.data.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository(private val database: FirebaseDatabase) {

    private val usersRef = database.getReference("usuarios")

    suspend fun getUserProfile(userId: String): User? {
        return try {
            val snapshot = usersRef.child(userId).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createUserProfile(user: FirebaseUser, userProfile: User): Boolean {
        return try {
            usersRef.child(user.uid).setValue(userProfile).await()
            true
        } catch (e: Exception) {
            false
        }
    }

}