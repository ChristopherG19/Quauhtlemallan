package com.app.quauhtlemallan.presentation.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.ui.theme.SelectedField
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.ui.theme.mossGreen
import com.hbb20.CountryCodePicker

@Composable
fun SignUpScreen(
    auth: FirebaseAuth,
    navigateBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("Guatemala") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón de regresar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_24),
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        navigateBack()  // Navegar hacia atrás
                    }
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Text(
            "Quauhtlemallan",
            color = Color.Black,
            fontSize = 24.sp,
            fontFamily = cinzelFontFamily,
            fontWeight = FontWeight.Normal
        )

        Spacer(Modifier.height(16.dp))

        // Campo de texto para el nombre de usuario (mismo formato que el Login)
        TextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = SelectedField
            ),
            placeholder = { Text(text = "Usuario", color = Color.Black) }
        )

        Spacer(Modifier.height(16.dp))

        // Campo de texto para el correo
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = SelectedField
            ),
            placeholder = { Text(text = "Correo", color = Color.Black) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(16.dp))

        // Campo de texto para la contraseña
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = SelectedField
            ),
            placeholder = { Text(text = "Contraseña", color = Color.Black) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(16.dp))

        // Campo de texto para confirmar la contraseña
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = SelectedField
            ),
            placeholder = { Text(text = "Confirmar Contraseña", color = Color.Black) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(16.dp))

        // Country Code Picker usando AndroidView
        AndroidView(
            factory = {
                CountryCodePicker(context).apply {
                    showFullName(true)
                    showNameCode(false)
                    setShowPhoneCode(false)
                    setCountryForNameCode("GT")  // Guatemala por defecto
                    setOnCountryChangeListener {
                        selectedCountry = selectedCountryName
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Botón de registro
        Button(
            onClick = {
                // Acción para registrar al usuario
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = mossGreen)
        ) {
            Text(text = "Registrar", color = Color.White)
        }
    }
}