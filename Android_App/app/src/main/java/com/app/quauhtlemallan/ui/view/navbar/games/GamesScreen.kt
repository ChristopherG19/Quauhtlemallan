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
                        text = "Pruebas contratiempo"
                    )
                }
                item {
                    GridItem(
                        imageRes = R.drawable.ic_question,
                        text = "Vos sabes qué hay en la imagen?"
                    )
                }
                item {
                    GridItem(
                        imageRes = R.drawable.ic_vof,
                        text = "Casaca o no"
                    )
                }
                item {
                    GridItem(
                        imageRes = R.drawable.ic_daily,
                        text = "Pregunta diaria"
                    )
                }
            }
        }
    }
}
