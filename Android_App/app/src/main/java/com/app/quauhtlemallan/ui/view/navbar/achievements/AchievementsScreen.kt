package com.app.quauhtlemallan.ui.view.navbar.achievements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.ui.view.navbar.BottomNavigationBar
import com.app.quauhtlemallan.data.model.AchievementData
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.ui.viewmodel.AchievementsViewModel
import com.app.quauhtlemallan.util.AchievementsState

@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel,
    categoryId: String,
    navController: NavHostController,
    navigateBack: () -> Unit
) {
    val badgesState by remember { viewModel.getBadgesByCategory(categoryId) }.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_24),
                    contentDescription = "Regresar",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navigateBack()
                        }
                )
            }

            when (badgesState) {
                is AchievementsState.Loading -> {
                }
                is AchievementsState.Success -> {
                    val badges = (badgesState as AchievementsState.Success).badges
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(badges) { badge ->
                            val progress by produceState(initialValue = 0) {
                                value = viewModel.getBadgeProgress(badge.id)
                            }
                            AchievementCard(badge = badge, progress)
                        }
                    }
                }
                is AchievementsState.Error -> {
                    val errorMessage = (badgesState as AchievementsState.Error).message
                    Text(text = errorMessage, color = Color.Red, fontFamily = cinzelFontFamily, fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}
