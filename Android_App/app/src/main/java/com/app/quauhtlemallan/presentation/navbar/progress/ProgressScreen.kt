package com.app.quauhtlemallan.presentation.navbar.progress

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.presentation.navbar.BottomNavigationBar
import com.app.quauhtlemallan.viewmodels.data.User

@Composable
fun ProgressScreen(navController: NavHostController, navigateToAchievements: () -> Unit) {
    val user = User(
        username = "Evan Peters",
        email = "evan123@gmail.com",
        country = "Guatemala",
        profileImage = "https://firebasestorage.googleapis.com/v0/b/quauhtlemallan-d86d0.appspot.com/o/ic_default.png?alt=media&token=4edc3e81-ecb0-4a88-8d46-8cf2c2dfc69e",
        score = 1200,
        achievements = 5
    )

    val user2 = User(
        username = "Evan Peters",
        email = "evan123@gmail.com",
        country = "Guatemala",
        profileImage = "https://firebasestorage.googleapis.com/v0/b/quauhtlemallan-d86d0.appspot.com/o/ic_default.png?alt=media&token=4edc3e81-ecb0-4a88-8d46-8cf2c2dfc69e",
        score = 1200,
        achievements = 5
    )

    val users = listOf(user, user2)

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Perfil del usuario
            UserProfileSection()

            // Botón para ver insignias
            Button(
                onClick = { navigateToAchievements() },
                modifier = Modifier
                    .width(150.dp)
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005F73))
            ) {
                Text(text = "Ver Insignias", color = Color.White)
            }

            // Título de la tabla de clasificaciones (arreglado para que abarque todo el ancho)
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Asegura que el Box ocupe todo el ancho
                    .background(Color(0xFF00A651)) // Color verde de fondo
                    .padding(vertical = 8.dp) // Padding vertical para el texto
            ) {
                Text(
                    text = "Tabla de clasificaciones: Guatexploradores",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center), // Centra el texto dentro del Box
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(users) { user ->
                    UserCard(user = user)
                }
            }
        }
    }
}

@Composable
fun UserProfileSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_default),
            contentDescription = "Imagen de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, shape = CircleShape)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Información del usuario
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "Evan Peters", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Nivel 2", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "Rank #1", fontSize = 16.sp)
        }
    }
}