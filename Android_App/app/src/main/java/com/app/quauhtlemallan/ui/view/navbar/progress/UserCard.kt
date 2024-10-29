package com.app.quauhtlemallan.ui.view.navbar.progress

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.model.User
import com.app.quauhtlemallan.ui.theme.Purple40
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserCard(user: User, rank: Int) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isCurrentUser = user.id == currentUserId

    val backgroundColor = if (isCurrentUser) Purple40 else Color.White
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val scoreTextColor = if (isCurrentUser) Color.White else Color.Gray

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
            Text(
                text = "#$rank",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = cinzelFontFamily,
                color = textColor,
                modifier = Modifier.padding(end = 16.dp)
            )

            Image(
                painter = rememberImagePainter(user.profileImage.ifEmpty{R.drawable.ic_default}),
                contentDescription = "Imagen de usuario",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Transparent, shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = user.username.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = cinzelFontFamily,
                    color = textColor
                )
                Text(
                    text = "Total de puntos: ${user.score}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = cinzelFontFamily,
                    color = scoreTextColor
                )
            }
        }
    }
}