package com.app.quauhtlemallan

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.presentation.navbar.BottomNavItem
import com.app.quauhtlemallan.presentation.home.HomeScreen
import com.app.quauhtlemallan.presentation.initial.InitialScreen
import com.app.quauhtlemallan.presentation.login.LoginScreen
import com.app.quauhtlemallan.presentation.navbar.achievements.AchievementsScreen
import com.app.quauhtlemallan.presentation.navbar.settings.SettingsScreen
import com.app.quauhtlemallan.presentation.navbar.chat.ChatScreen
import com.app.quauhtlemallan.presentation.navbar.games.GamesScreen
import com.app.quauhtlemallan.presentation.navbar.progress.ProgressScreen
import com.app.quauhtlemallan.presentation.signup.SignUpScreen
import com.app.quauhtlemallan.viewmodels.authentication.LoginViewModel
import com.app.quauhtlemallan.viewmodels.authentication.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth
) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val currentUser = auth.currentUser

    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
    googleSignInClient.signOut()

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
                googleSignInClient = googleSignInClient,
                database = database
            )
        }
        composable("signUp"){
            val signUpViewModel = viewModel<RegisterViewModel>()

            SignUpScreen(
                auth = auth,
                database = database,
                viewModel = signUpViewModel,
                navigateBack = { navHostController.navigate("initial") },
                navigateToHome = {
                    navHostController.navigate("home") {
                        popUpTo("initial") { inclusive = true }
                    }
                }
            )
        }
        composable("home"){
            HomeScreen(
                navController = navHostController
            )
        }
        composable(BottomNavItem.Progreso.route) {
            ProgressScreen(
                navController = navHostController,
                navigateToAchievements = { navHostController.navigate("achievements")}
            )
        }
        composable(BottomNavItem.Chat.route) {
            ChatScreen(navController = navHostController)
        }
        composable(BottomNavItem.Inicio.route) {
            HomeScreen(
                navController = navHostController
            )
        }
        composable(BottomNavItem.Juegos.route) {
            GamesScreen(navController = navHostController)
        }
        composable(BottomNavItem.Ajustes.route) {
            SettingsScreen(
                auth = auth,
                googleSignInClient = googleSignInClient,
                navController = navHostController
            )
        }
        composable("achievements") {
            AchievementsScreen(
            navController = navHostController,
            navigateBack = { navHostController.navigate(BottomNavItem.Progreso.route) }
            )
        }
    }
}