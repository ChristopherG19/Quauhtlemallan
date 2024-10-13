package com.app.quauhtlemallan.ui.view.navbar.progress

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.app.quauhtlemallan.ui.view.navbar.BottomNavigationBar
import com.app.quauhtlemallan.data.model.User
import com.app.quauhtlemallan.ui.theme.bottleGreen
import com.app.quauhtlemallan.ui.theme.deepBlue
import com.app.quauhtlemallan.ui.theme.mossGreen
import com.app.quauhtlemallan.ui.theme.seaGreen
import com.app.quauhtlemallan.ui.theme.standardGreen
import com.app.quauhtlemallan.ui.viewmodel.ProgressState
import com.app.quauhtlemallan.ui.viewmodel.ProgressViewModel

@Composable
fun ProgressScreen(
    navController: NavHostController,
    viewModel: ProgressViewModel,
    navigateToAchievements: () -> Unit
) {
    val progressState by viewModel.progressState.collectAsState()
    val userProfile = viewModel.userProfile
    val users = viewModel.users

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            when (progressState) {
                is ProgressState.Loading -> {
                    CircularProgressIndicator()
                }
                is ProgressState.Error -> {
                    val message = (progressState as ProgressState.Error).message
                    Text(text = message, color = Color.Red)
                }
                else -> {
                    val userRank = users.indexOfFirst { it.id == userProfile?.id } + 1

                    userProfile?.let {
                        UserProfileSection(userProfile = it, rank = userRank)
                    }

                    // Botón para ver insignias
                    Button(
                        onClick = { navigateToAchievements() },
                        modifier = Modifier
                            .width(150.dp)
                            .padding(vertical = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(containerColor = deepBlue)
                    ) {
                        Text(text = "Ver Insignias", color = Color.White)
                    }

                    // Título de la tabla de clasificaciones
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(mossGreen)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Tabla de clasificaciones: Guatexploradores",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lista de usuarios ordenados por puntaje
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(users) { index, user ->
                            UserCard(user = user, rank = index + 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileSection(userProfile: User, rank: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = rememberImagePainter(userProfile.profileImage),
            contentDescription = "Imagen de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Transparent, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = userProfile.username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Nivel 2", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "Rank #$rank", fontSize = 16.sp)
        }
    }
}