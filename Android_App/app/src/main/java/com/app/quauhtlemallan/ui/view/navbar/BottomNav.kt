package com.app.quauhtlemallan.ui.view.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.quauhtlemallan.R
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
        BottomNavItem.Perfil
    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.title,
                        modifier = Modifier.size(28.dp)
                    )},
                label = { Text(text = item.title, fontFamily = cinzelFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified,
                    indicatorColor = lightGreen
                )
            )
        }
    }
}

sealed class BottomNavItem(val title: String, val iconResId: Int, val route: String) {
    object Progreso : BottomNavItem("Progreso", R.drawable.ic_progress, "progreso")
    object Chat : BottomNavItem("Kukul", R.drawable.ic_chat, "chat")
    object Inicio : BottomNavItem("Inicio", R.drawable.ic_home, "inicio")
    object Juegos : BottomNavItem("Juegos", R.drawable.ic_games, "juegos")
    object Perfil : BottomNavItem("Perfil", R.drawable.ic_settings, "perfil")
}
