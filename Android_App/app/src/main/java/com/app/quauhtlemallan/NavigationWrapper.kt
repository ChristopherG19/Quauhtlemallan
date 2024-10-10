package com.app.quauhtlemallan

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.presentation.home.AjustesScreen
import com.app.quauhtlemallan.presentation.home.BottomNavItem
import com.app.quauhtlemallan.presentation.home.ChatScreen
import com.app.quauhtlemallan.presentation.home.HomeScreen
import com.app.quauhtlemallan.presentation.home.InicioScreen
import com.app.quauhtlemallan.presentation.home.JuegosScreen
import com.app.quauhtlemallan.presentation.home.ProgresoScreen
import com.app.quauhtlemallan.presentation.initial.InitialScreen
import com.app.quauhtlemallan.presentation.login.LoginScreen
import com.app.quauhtlemallan.presentation.signup.SignUpScreen
import com.app.quauhtlemallan.viewmodels.login.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth
) {
    val context = LocalContext.current
    val currentUser = auth.currentUser

    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    NavHost(navController = navHostController, startDestination = "initial"){
        composable("initial"){
            InitialScreen(navigateToLogin = {navHostController.navigate("logIn")},
                navigateToSignUp = {navHostController.navigate("signUp")})
        }
        composable("logIn"){
            val loginViewModel = viewModel<LoginViewModel>()

            LoginScreen(
                auth = auth,
                viewModel = loginViewModel,
                navigateToHome = { navHostController.navigate("home") },
                navigateBack = { navHostController.navigate("initial") },
                googleSignInClient = googleSignInClient
            )
        }
        composable("signUp"){
            SignUpScreen(auth)
        }
        composable("home"){
            HomeScreen(
                auth = auth,
                googleSignInClient = googleSignInClient,
                navigateToLogin = { navHostController.navigate("logIn") },
                navController = navHostController
            )
        }

        composable(BottomNavItem.Progreso.route) {
            ProgresoScreen()
        }
        composable(BottomNavItem.Chat.route) {
            ChatScreen()
        }
        composable(BottomNavItem.Inicio.route) {
            InicioScreen()
        }
        composable(BottomNavItem.Juegos.route) {
            JuegosScreen()
        }
        composable(BottomNavItem.Ajustes.route) {
            AjustesScreen()
        }
    }
}