package com.app.quauhtlemallan

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.app.quauhtlemallan.utils.showAlert
import com.app.quauhtlemallan.viewmodels.login.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { taskG ->
                            if (taskG.isSuccessful) {
                                // Aquí puedes proceder con la navegación a Home
                                navHostController.navigate("home")
                            } else {
                                showAlert(context, "Error de Autenticación", "No se pudo autenticar con Google.")
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert(context, "Error de Google Sign-In", e.message ?: "Error desconocido.")
            }
        }
    )

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
                onGoogleSignInClick = {
                    launcher.launch(googleSignInClient.signInIntent)
                }
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