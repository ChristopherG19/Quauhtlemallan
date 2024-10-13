package com.app.quauhtlemallan

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.data.repository.UserRepository
import com.app.quauhtlemallan.ui.view.navbar.BottomNavItem
import com.app.quauhtlemallan.ui.view.home.HomeScreen
import com.app.quauhtlemallan.ui.view.initial.InitialScreen
import com.app.quauhtlemallan.ui.view.login.LoginScreen
import com.app.quauhtlemallan.ui.view.navbar.achievements.AchievementsScreen
import com.app.quauhtlemallan.ui.view.navbar.settings.SettingsScreen
import com.app.quauhtlemallan.ui.view.navbar.chat.ChatScreen
import com.app.quauhtlemallan.ui.view.navbar.games.GamesScreen
import com.app.quauhtlemallan.ui.view.navbar.progress.ProgressScreen
import com.app.quauhtlemallan.ui.view.signup.SignUpScreen
import com.app.quauhtlemallan.ui.viewmodel.LoginViewModel
import com.app.quauhtlemallan.ui.viewmodel.ProgressViewModel
import com.app.quauhtlemallan.ui.viewmodel.RegisterViewModel
import com.app.quauhtlemallan.ui.viewmodel.SettingsViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth
) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val storage = FirebaseStorage.getInstance()
    val userRepository = UserRepository(auth, database, storage)

    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    val loginViewModelFactory = LoginViewModelFactory(auth, userRepository)
    val registerViewModelFactory = RegisterViewModelFactory(auth, userRepository)
    val settingsViewModelFactory = SettingsViewModelFactory(userRepository)
    val progressViewModelFactory = ProgressViewModelFactory(userRepository)

    NavHost(navController = navHostController, startDestination = "initial"){
        composable("initial"){
            InitialScreen(navigateToLogin = {navHostController.navigate("logIn")},
                navigateToSignUp = {navHostController.navigate("signUp")})
        }
        composable("logIn"){
            val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)
            LoginScreen(
                viewModel = loginViewModel,
                navigateToHome = { navHostController.navigate("home") },
                navigateBack = { navHostController.navigate("initial") },
                googleSignInClient = googleSignInClient
            )
        }
        composable("signUp"){
            val signUpViewModel: RegisterViewModel = viewModel(factory = registerViewModelFactory)
            SignUpScreen(
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
            val progressViewModel: ProgressViewModel = viewModel(factory = progressViewModelFactory)
            ProgressScreen(
                navController = navHostController,
                viewModel = progressViewModel,
                navigateToAchievements = { navHostController.navigate("achievements") }
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
            val settingsViewModel: SettingsViewModel = viewModel(factory = settingsViewModelFactory)
            SettingsScreen(
                auth = auth,
                viewModel = settingsViewModel,
                navController = navHostController,
                googleSignInClient = googleSignInClient
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