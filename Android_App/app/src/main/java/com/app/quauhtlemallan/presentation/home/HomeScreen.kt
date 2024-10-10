package com.app.quauhtlemallan.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient,
    navigateToLogin: () -> Unit,
    navController: NavHostController
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Bienvenido a Quahtlemallan")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        auth.signOut()
                        googleSignInClient.signOut().addOnCompleteListener {
                            navigateToLogin()
                        }
                    }
                ) {
                    Text(text = "Cerrar sesión")
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Progreso,
        BottomNavItem.Chat,
        BottomNavItem.Inicio,
        BottomNavItem.Juegos,
        BottomNavItem.Ajustes
    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.Inicio.route) {
        composable(BottomNavItem.Progreso.route) { ProgresoScreen() }
        composable(BottomNavItem.Chat.route) { ChatScreen() }
        composable(BottomNavItem.Inicio.route) { InicioScreen() }
        composable(BottomNavItem.Juegos.route) { JuegosScreen() }
        composable(BottomNavItem.Ajustes.route) { AjustesScreen() }
    }
}

@Composable
fun ProgresoScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla de Progreso")
    }
}

@Composable
fun ChatScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla de Chat")
    }
}

@Composable
fun InicioScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla de Inicio")
    }
}

@Composable
fun JuegosScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla de Juegos")
    }
}

@Composable
fun AjustesScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla de Ajustes")
    }
}

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Progreso : BottomNavItem("Progreso", Icons.Default.Menu, "progreso")
    object Chat : BottomNavItem("Chat", Icons.Default.MailOutline, "chat")
    object Inicio : BottomNavItem("Inicio", Icons.Default.Home, "inicio")
    object Juegos : BottomNavItem("Juegos", Icons.Default.Star, "juegos")
    object Ajustes : BottomNavItem("Ajustes", Icons.Default.Settings, "ajustes")
}