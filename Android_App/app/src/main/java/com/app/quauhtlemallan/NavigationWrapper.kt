package com.app.quauhtlemallan

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.presentation.bottomnav.BottomNavItem
import com.app.quauhtlemallan.presentation.home.HomeScreen
import com.app.quauhtlemallan.presentation.initial.InitialScreen
import com.app.quauhtlemallan.presentation.login.LoginScreen
import com.app.quauhtlemallan.presentation.navbar.ajustes.SettingsScreen
import com.app.quauhtlemallan.presentation.navbar.chat.ChatScreen
import com.app.quauhtlemallan.presentation.navbar.games.GamesScreen
import com.app.quauhtlemallan.presentation.navbar.progreso.ProgresoScreen
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
                navigateToStart = { navHostController.navigate("initial") },
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
            HomeScreen(
                auth = auth,
                googleSignInClient = googleSignInClient,
                navigateToStart = { navHostController.navigate("home") },
                navController = navHostController
            )
        }
        composable(BottomNavItem.Juegos.route) {
            GamesScreen()
        }
        composable(BottomNavItem.Ajustes.route) {
            SettingsScreen()
        }
    }
}