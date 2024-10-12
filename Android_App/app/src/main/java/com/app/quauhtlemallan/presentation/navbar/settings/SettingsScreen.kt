package com.app.quauhtlemallan.presentation.navbar.settings

import android.content.Context
import android.widget.LinearLayout
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.presentation.navbar.BottomNavigationBar
import com.app.quauhtlemallan.ui.theme.forestGreen
import com.app.quauhtlemallan.ui.theme.crimsonRed
import com.app.quauhtlemallan.ui.theme.navyBlue
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.hbb20.CountryCodePicker

@Composable
fun SettingsScreen(
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient,
    navController: NavHostController
) {
    var username by remember { mutableStateOf("Evan Peters") }
    var email by remember { mutableStateOf("evan123@gmail.com") }
    var password by remember { mutableStateOf("***********") }
    var selectedCountry by remember { mutableStateOf("Guatemala") }
    val context = LocalContext.current  // Obtener el contexto actual

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenedor para la imagen de perfil y el botón de cambiar foto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                // Imagen de perfil
                Image(
                    painter = painterResource(id = R.drawable.ic_default),  // Reemplaza con tu imagen
                    contentDescription = "Imagen de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Black, shape = CircleShape)
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { /* Acción para cambiar la foto */ },
                    colors = ButtonDefaults.buttonColors(containerColor = navyBlue)
                ) {
                    Text(text = "Cambiar Foto", color = Color.White)
                }
            }

            // Campo de texto para el nombre de usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("Usuario") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
            )

            // Campo de texto para el correo (deshabilitado)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("Correo") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                enabled = false,  // Campo deshabilitado
            )

            // Campo de texto para la nueva contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),  // Oculta el texto
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )

            // Contenedor con el título "País de origen" y borde alrededor de CountryCodePicker
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "País de origen",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Borde y estilo similar a OutlinedTextField para CountryCodePicker
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    CountryCodePickerView(
                        context = context,
                        onCountrySelected = { selectedCountry = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Guardar Cambios
            Button(
                onClick = { /* Acción para guardar cambios */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = forestGreen)
            ) {
                Text(text = "Guardar Cambios", color = Color.White)
            }

            // Botón Cerrar Sesión
            Button(
                onClick = {
                    auth.signOut()
                    googleSignInClient.signOut().addOnCompleteListener {
                        navController.navigate("initial") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = crimsonRed)
            ) {
                Text(text = "Cerrar sesión", color = Color.White)
            }
        }
    }
}

// Composable para integrar el CountryCodePicker de Android usando AndroidView
@Composable
fun CountryCodePickerView(
    context: Context,
    onCountrySelected: (String) -> Unit
) {
    AndroidView(
        factory = {
            // Crear una instancia del CountryCodePicker
            CountryCodePicker(context).apply {
                showFlag(true)
                showFullName(true)
                setCountryForNameCode("GT")
                setShowPhoneCode(false)

                setOnCountryChangeListener {
                    onCountrySelected(selectedCountryName)
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
        },
        update = { view ->
            // Puedes agregar lógica adicional para actualizar la vista si es necesario
        }
    )
}