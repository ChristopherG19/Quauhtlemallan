package com.app.quauhtlemallan.util

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.app.quauhtlemallan.data.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

fun showAlert(context: Context, title: String, message: String) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Aceptar", null)
        .show()
}


object Util {

    fun createUserProfile(
        user: FirebaseUser,
        provider: ProviderType,
        database: FirebaseDatabase,
        isRegistration: Boolean,
        username: String,
        country: String,
        onProfileCreated: (User) -> Unit
    ) {
        val usersRef = database.getReference("usuarios")
        val userId = user.uid

        usersRef.child(userId).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                val emailToUse = user.email ?: ""
                val usernameToUse = if (isRegistration) {
                    username
                } else {
                    user.displayName ?: ""
                }

                val userProfile = User(
                    id = userId,
                    username = usernameToUse,
                    email = emailToUse,
                    country = country,
                    profileImage = user.photoUrl?.toString() ?: "https://firebasestorage.googleapis.com/v0/b/quauhtlemallan-d86d0.appspot.com/o/ic_default.png?alt=media&token=4edc3e81-ecb0-4a88-8d46-8cf2c2dfc69e"
                )

                usersRef.child(userId).setValue(userProfile).addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        onProfileCreated(userProfile)
                    } else {
                        Log.e("Util", "Error al guardar el perfil en Firebase")
                    }
                }
            } else {
                val userProf = snapshot.getValue(User::class.java)
                userProf?.let {
                    onProfileCreated(it)
                }
            }
        }
    }
}
