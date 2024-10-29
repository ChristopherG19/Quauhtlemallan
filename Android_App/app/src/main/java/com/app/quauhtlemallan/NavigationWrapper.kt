package com.app.quauhtlemallan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.data.repository.QuestionRepository
import com.app.quauhtlemallan.data.repository.UserRepository
import com.app.quauhtlemallan.ui.view.navbar.BottomNavItem
import com.app.quauhtlemallan.ui.view.home.HomeScreen
import com.app.quauhtlemallan.ui.view.initial.InitialScreen
import com.app.quauhtlemallan.ui.view.login.LoginScreen
import com.app.quauhtlemallan.ui.view.navbar.achievements.AchievementsScreen
import com.app.quauhtlemallan.ui.view.navbar.achievements.CategoriesScreen
import com.app.quauhtlemallan.ui.view.navbar.chat.ChatScreen
import com.app.quauhtlemallan.ui.view.navbar.chat.TraduccionesScreen
import com.app.quauhtlemallan.ui.view.navbar.games.CategoriesGamesScreen
import com.app.quauhtlemallan.ui.view.navbar.games.CategoryGameScreen
import com.app.quauhtlemallan.ui.view.navbar.games.DailyQuestionScreen
import com.app.quauhtlemallan.ui.view.navbar.games.GamesScreen
import com.app.quauhtlemallan.ui.view.navbar.games.TimeQuestionScreen
import com.app.quauhtlemallan.ui.view.navbar.games.TrueFalseGameScreen
import com.app.quauhtlemallan.ui.view.navbar.profile.ProfileScreen
import com.app.quauhtlemallan.ui.view.navbar.progress.ProgressScreen
import com.app.quauhtlemallan.ui.view.signup.SignUpScreen
import com.app.quauhtlemallan.ui.viewmodel.AchievementsViewModel
import com.app.quauhtlemallan.ui.viewmodel.CategoryGameViewModel
import com.app.quauhtlemallan.ui.viewmodel.ChatViewModel
import com.app.quauhtlemallan.ui.viewmodel.DailyQuestionViewModel
import com.app.quauhtlemallan.ui.viewmodel.HomeViewModel
import com.app.quauhtlemallan.ui.viewmodel.LoginViewModel
import com.app.quauhtlemallan.ui.viewmodel.ProgressViewModel
import com.app.quauhtlemallan.ui.viewmodel.RegisterViewModel
import com.app.quauhtlemallan.ui.viewmodel.SettingsViewModel
import com.app.quauhtlemallan.ui.viewmodel.TimeQuestionViewModel
import com.app.quauhtlemallan.ui.viewmodel.TraduccionesViewModel
import com.app.quauhtlemallan.ui.viewmodel.TrueFalseGameViewModel
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
    val achievementsViewModelFactory = AchievementsViewModelFactory(userRepository)
    val chatViewModelFactory = ChatViewModelFactory()
    val dailyQuestionViewModelFactory = DailyQuestionViewModelFactory(userRepository)
    val trueFalseGameViewModelFactory = TrueFalseGameViewModelFactory(QuestionRepository(),userRepository)
    val timeQuestionViewModelFactory = TimeQuestionViewModelFactory(QuestionRepository(), userRepository)
    val categoryGameViewModelFactory = CategoryGameViewModelFactory(QuestionRepository(), userRepository)
    val homeViewModelFactory = HomeViewModelFactory(userRepository)

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
            val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
            val achievementsViewModel: AchievementsViewModel = viewModel(factory = achievementsViewModelFactory)
            val progress by achievementsViewModel.discoveryPercentage
            HomeScreen(
                navController = navHostController,
                userPercentage = progress.toFloat(),
                homeViewModel
            )
        }

        composable(BottomNavItem.Progreso.route) {
            val progressViewModel: ProgressViewModel = viewModel(factory = progressViewModelFactory)
            ProgressScreen(
                navController = navHostController,
                viewModel = progressViewModel,
                navigateToAchievements = { navHostController.navigate("categories") }
            )
        }

        composable(BottomNavItem.Chat.route) {
            val chatViewModel: ChatViewModel = viewModel(factory = chatViewModelFactory)
            ChatScreen(
                navController = navHostController,
                viewModel = chatViewModel
            )
        }

        composable("traducciones") {
            val traduccionesViewModel = viewModel<TraduccionesViewModel>()
            TraduccionesScreen(navController = navHostController, traduccionesViewModel = traduccionesViewModel)
        }

        composable(BottomNavItem.Inicio.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
            val achievementsViewModel: AchievementsViewModel = viewModel(factory = achievementsViewModelFactory)
            val progress by achievementsViewModel.discoveryPercentage
            HomeScreen(
                navController = navHostController,
                userPercentage = progress.toFloat(),
                homeViewModel
            )
        }

        composable(BottomNavItem.Juegos.route) {
            GamesScreen(navController = navHostController)
        }

        composable(BottomNavItem.Perfil.route) {
            val settingsViewModel: SettingsViewModel = viewModel(factory = settingsViewModelFactory)
            ProfileScreen(
                auth = auth,
                viewModel = settingsViewModel,
                navController = navHostController,
                googleSignInClient = googleSignInClient
            )
        }

        composable("categories") {
            val achievementsViewModel: AchievementsViewModel = viewModel(factory = achievementsViewModelFactory)
            val progress by achievementsViewModel.discoveryPercentage
            CategoriesScreen(
                viewModel = achievementsViewModel,
                navController = navHostController,
                progress = progress.toFloat(),
                navigateBack = { navHostController.navigateUp() }
            )
        }

        composable("badges/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val achievementsViewModel: AchievementsViewModel = viewModel(factory = achievementsViewModelFactory)
            AchievementsScreen(
                viewModel = achievementsViewModel,
                categoryId = categoryId,
                navController = navHostController,
                navigateBack = { navHostController.navigateUp() }
            )
        }

        composable("timeScreen") {
            val timeQuestionViewModel: TimeQuestionViewModel = viewModel(factory = timeQuestionViewModelFactory)
            TimeQuestionScreen(
                viewModel = timeQuestionViewModel,
                navController = navHostController,
                navigateBack = { navHostController.navigateUp() },
                onGameEnd = { navHostController.navigate(BottomNavItem.Juegos.route) }
            )
        }

        composable("categoryScreen") {
            CategoriesGamesScreen(
                navController = navHostController,
                navigateBack = { navHostController.navigateUp() }
            )
        }

        composable("games/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryGameViewModel: CategoryGameViewModel = viewModel(factory = categoryGameViewModelFactory)
            CategoryGameScreen(
                viewModel = categoryGameViewModel,
                categoryId = categoryId,
                navController = navHostController,
                navigateBack = { navHostController.navigateUp() }
            )
        }

        composable("tofScreen") {
            val tofViewModel: TrueFalseGameViewModel = viewModel(factory = trueFalseGameViewModelFactory)
            TrueFalseGameScreen(
                viewModel = tofViewModel,
                navController = navHostController,
                onGameEnd = { navHostController.navigate(BottomNavItem.Juegos.route) }
            )
        }

        composable("dailyQuestionScreen") {
            val dailyQuestionViewModel: DailyQuestionViewModel = viewModel(factory = dailyQuestionViewModelFactory)
            DailyQuestionScreen(
                viewModel = dailyQuestionViewModel,
                navController = navHostController,
                navigateToNextQ = { navHostController.navigate("dailyQuestionScreen") },
                navigateBack = { navHostController.navigateUp() }
            )
        }
    }
}