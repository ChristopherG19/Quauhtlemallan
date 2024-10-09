package com.app.quauhtlemallan.viewmodels.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.app.quauhtlemallan.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {

    suspend fun signIn(): IntentSender? {
        return try {
            val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(context.getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()

            val result = oneTapClient.beginSignIn(signInRequest).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun signInWithIntent(data: Intent?): SignInResult {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val googleIdToken = credential.googleIdToken
            val userId = credential.id
            SignInResult(
                data = UserData(userId, credential.displayName, credential.profilePictureUri.toString()),
                errorMessage = null
            )
        } catch (e: Exception) {
            SignInResult(data = null, errorMessage = e.localizedMessage)
        }
    }

    fun getSignedInUser(): UserData? {
        // Aquí puedes implementar cómo obtienes los datos del usuario autenticado
        return null
    }
}
