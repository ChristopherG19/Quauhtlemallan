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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.model.Category
import com.app.quauhtlemallan.data.model.User
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.ui.view.navbar.BottomNavigationBar
import com.app.quauhtlemallan.ui.viewmodel.AchievementsViewModel

@Composable
fun CategoriesScreen(
    viewModel: AchievementsViewModel,
    navController: NavHostController,
    progress: Float,
//    user: User,
    navigateBack: () -> Unit
) {
    val categories = listOf(
        Category(id = "artes", title = "Artes", imageUrl = R.drawable.ic_arte),
        Category(id = "cultura", title = "Cultura", imageUrl = R.drawable.ic_cultura),
        Category(id = "deportes", title = "Deportes", imageUrl = R.drawable.ic_deporte),
        Category(id = "gastronomia", title = "Gastronomía", imageUrl = R.drawable.ic_comida),
        Category(id = "geografia", title = "Geografía", imageUrl = R.drawable.ic_geo),
        Category(id = "historia", title = "Historia", imageUrl = R.drawable.ic_historia),
        Category(id = "idiomas", title = "Idiomas", imageUrl = R.drawable.ic_idiomas),
        Category(id = "turismo", title = "Turismo", imageUrl = R.drawable.ic_turismo)
    )

    val borderColors = listOf(
        Color(0xFFEBB006),
        Color(0xFFCA6702),
        Color(0xFF005F73),
        Color(0xFF94D2BD),
        Color(0xFF7D5260),
        Color(0xFFEE9B00),
        Color(0xFF184E77),
        Color(0xFFB5E48C)
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
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
                Spacer(modifier = Modifier.weight(1f))
            }

            // Indicador de progreso
            LinearProgressIndicator(
                progress = { (progress / 100) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${progress.toInt()}%",
                fontFamily = cinzelFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(categories.size) { index ->
                    CategoryCard(
                        category = categories[index],
                        borderColor = borderColors[index],
                        onClick = {
                            navController.navigate("badges/${categories[index].id}")
                        }
                    )
                }
            }
        }
    }
}