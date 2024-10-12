package com.app.quauhtlemallan.presentation.navbar.achievements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.presentation.navbar.BottomNavigationBar
import com.app.quauhtlemallan.viewmodels.data.AchievementData

@Composable
fun AchievementsScreen(navController: NavHostController) {
    val progress = 22f  // Valor de progreso
    val badges = listOf(
        AchievementData(1, "Categoría 1", R.drawable.ic_home),
        AchievementData(2, "Categoría 2", R.drawable.ic_home),
        AchievementData(3, "Categoría 3", R.drawable.ic_home),
        AchievementData(4, "Categoría 4", R.drawable.ic_home),
        // Agrega más insignias según sea necesario
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicador de progreso
            LinearProgressIndicator(
                progress = { progress / 100 },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${progress.toInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Cuadrícula de insignias
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(badges) { badge ->
                    AchievementCard(badge = badge)
                }
            }
        }
    }
}