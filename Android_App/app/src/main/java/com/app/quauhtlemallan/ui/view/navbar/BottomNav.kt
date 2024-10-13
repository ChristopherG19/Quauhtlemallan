package com.app.quauhtlemallan.ui.view.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.ui.theme.forestGreen
import com.app.quauhtlemallan.ui.theme.lightGreen

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
                label = { Text(text = item.title, fontFamily = cinzelFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = forestGreen,
                    indicatorColor = lightGreen
                )
            )
        }
    }
}

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Progreso : BottomNavItem("Progreso", Icons.Default.Menu, "progreso")
    object Chat : BottomNavItem("Chat", Icons.Default.MailOutline, "chat")
    object Inicio : BottomNavItem("Inicio", Icons.Default.Home, "inicio")
    object Juegos : BottomNavItem("Juegos", Icons.Default.Star, "juegos")
    object Ajustes : BottomNavItem("Ajustes", Icons.Default.Settings, "ajustes")
}