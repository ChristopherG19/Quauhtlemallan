package com.app.quauhtlemallan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.quauhtlemallan.ui.theme.QuauhtlemallanTheme
import com.app.quauhtlemallan.ui.view.login.LoginScreen
import com.app.quauhtlemallan.util.SessionManager
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity: ComponentActivity() {

    private lateinit var navHostController: NavHostController
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AppEventsLogger.activateApp(application)

        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        sessionManager = SessionManager(applicationContext)

        setContent {
            navHostController = rememberNavController()
            QuauhtlemallanTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    LaunchedEffect(Unit) {
                        sessionManager.isLoggedIn.collect { isLoggedIn ->
                            if (isLoggedIn) {
                                navHostController.navigate("home") {
                                    popUpTo("initial") { inclusive = true }
                                }
                            } else {
                                navHostController.navigate("initial")
                            }
                        }
                    }

                    NavigationWrapper(navHostController, auth, sessionManager)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LoginScreen.callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        val currentUser:FirebaseUser? = auth.currentUser
    }

}