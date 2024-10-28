package com.app.quauhtlemallan.ui.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import coil.size.OriginalSize
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.model.User
import com.app.quauhtlemallan.ui.view.navbar.BottomNavigationBar
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily

@Composable
fun HomeScreen(
    navController: NavHostController,
    userPercentage: Float
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quahtlemallan",
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontFamily = cinzelFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = R.drawable.ic_quetzal,
                        contentDescription = "Gif Image",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.mapa)
                        .decoderFactory(GifDecoder.Factory())
                        .size(360)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Mapa GIF",
                    modifier = Modifier
                        .size(360.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = painterResource(id = R.drawable.static_placeholder),
                    error = painterResource(id = R.drawable.static_placeholder),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(32.dp))

                LinearProgressIndicator(
                    progress = userPercentage / 100f,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .background(Color.Transparent)
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${userPercentage.toInt()}% DESCUBIERTO",
                        fontFamily = cinzelFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }
}