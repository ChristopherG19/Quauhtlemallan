package com.app.quauhtlemallan.ui.view.navbar.achievements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.model.AchievementData
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.google.android.material.progressindicator.LinearProgressIndicator

@Composable
fun AchievementCard(badge: AchievementData) {
    val backgroundColor = Color.White
    val textColor = Color.Black
    val descriptionColor = Color.Gray

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la insignia
            Image(
                painter = rememberImagePainter(badge.imageUrl.ifEmpty { R.drawable.ic_default }),
                contentDescription = "Imagen de la insignia",
                modifier = Modifier
                    .size(50.dp)
                    .border(2.dp, Color.Transparent)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Texto con la información de la insignia
            Column {
                Text(
                    text = badge.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = cinzelFontFamily,
                    color = textColor
                )
                Text(
                    text = badge.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = cinzelFontFamily,
                    color = descriptionColor
                )
            }
        }
    }
}
