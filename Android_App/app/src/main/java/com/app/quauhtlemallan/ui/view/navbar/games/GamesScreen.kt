package com.app.quauhtlemallan.ui.view.navbar.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.ui.theme.brickRed
import com.app.quauhtlemallan.ui.theme.paleGreen
import com.app.quauhtlemallan.ui.theme.sunflowerYellow
import com.app.quauhtlemallan.ui.theme.oceanBlue
import com.app.quauhtlemallan.ui.view.navbar.BottomNavigationBar

@Composable
fun GamesScreen (
    navController: NavHostController
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    GridItem(
                        imageRes = R.drawable.ic_time,
                        text = "Pruebas contratiempo",
                        color = oceanBlue,
                        onClick = { navController.navigate("dailyQuestionScreen") }
                    )
                }
                item {
                    GridItem(
                        imageRes = R.drawable.ic_category,
                        text = "Preguntas por categoría",
                        color = paleGreen,
                        onClick = { navController.navigate("dailyQuestionScreen") }
                    )
                }
                item {
                    GridItem(
                        imageRes = R.drawable.ic_vof,
                        text = "Casaca o no",
                        color = brickRed,
                        onClick = { navController.navigate("tofScreen") }
                    )
                }
                item {
                    GridItem(
                        imageRes = R.drawable.ic_daily,
                        text = "Pregunta diaria",
                        color = sunflowerYellow,
                        onClick = { navController.navigate("dailyQuestionScreen") }
                    )
                }
            }
        }
    }
}
