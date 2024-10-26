package com.app.quauhtlemallan.ui.view.navbar.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.ui.viewmodel.TraduccionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraduccionesScreen(
    navController: NavHostController,
    traduccionesViewModel: TraduccionesViewModel
) {
    val traducciones by traduccionesViewModel.traducciones.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    val selectedTabIndex by traduccionesViewModel.selectedTabIndex.collectAsState()
    val idiomas by traduccionesViewModel.idiomas.collectAsState(initial = emptyList())

    if (idiomas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(
                        "Traducciones a lenguas mayas",
                        fontFamily = cinzelFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    )},
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(8.dp)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 8.dp,
                    contentColor = Color.Black
                ) {
                    idiomas.forEachIndexed { index, idioma ->
                        Tab(
                            text = { Text(
                                idioma,
                                fontFamily = cinzelFontFamily,
                                fontWeight = FontWeight.Normal
                            )},
                            selected = selectedTabIndex == index,
                            onClick = { traduccionesViewModel.onTabSelected(index) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de búsqueda
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(
                        "Buscar traducción...",
                        fontFamily = cinzelFontFamily,
                        fontWeight = FontWeight.Normal
                    )},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                val traduccionesFiltradas = traducciones.filter { traduccion ->
                    traduccion.lengua == idiomas[selectedTabIndex] &&
                            (traduccion.espanol.contains(searchQuery, ignoreCase = true) ||
                                    traduccion.lenguaMaya.contains(searchQuery, ignoreCase = true))
                }

                LazyColumn {
                    items(traduccionesFiltradas) { traduccion ->
                        var espanol = traduccion.espanol
                        var lenguaMaya = traduccion.lenguaMaya
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Español: $espanol",
                                    fontFamily = cinzelFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Traducción: $lenguaMaya",
                                    fontFamily = cinzelFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
