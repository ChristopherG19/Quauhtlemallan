package com.app.quauhtlemallan.ui.view.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.ui.theme.SelectedField
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.data.model.User
import com.app.quauhtlemallan.ui.theme.mossGreen
import com.app.quauhtlemallan.ui.theme.rubikFontFamily
import com.app.quauhtlemallan.ui.viewmodel.RegisterViewModel
import com.app.quauhtlemallan.util.RegisterState
import com.hbb20.CountryCodePicker

@Composable
fun SignUpScreen(
    viewModel: RegisterViewModel,
    navigateBack: () -> Unit,
    navigateToHome: (User) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.registerStateFlow.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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

        TextField(
            value = viewModel.username,
            onValueChange = viewModel::onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Black
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
            value = viewModel.email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Black
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
            value = viewModel.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = SelectedField
            ),
            placeholder = { Text(text = "Contraseña", color = Color.Black) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) {
                    painterResource(id = R.drawable.ic_visibility_off)
                } else {
                    painterResource(id = R.drawable.ic_visibility)
                }

                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(painter = image, contentDescription = "Toggle password visibility", tint = Color.Black)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(16.dp))

        // Campo de texto para confirmar la contraseña
        TextField(
            value = viewModel.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = SelectedField
            ),
            placeholder = { Text(text = "Confirmar Contraseña", color = Color.Black) },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible) {
                    painterResource(id = R.drawable.ic_visibility_off)
                } else {
                    painterResource(id = R.drawable.ic_visibility)
                }

                IconButton(onClick = {
                    confirmPasswordVisible = !confirmPasswordVisible
                }) {
                    Icon(painter = image, contentDescription = "Toggle confirm password visibility", tint = Color.Black)
                }
            },
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
                    setCountryForNameCode("GT")
                    setOnCountryChangeListener {
                        viewModel.onCountryChange(selectedCountryName)
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
                viewModel.registerUser { user ->
                    navigateToHome(user)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)
                .background(mossGreen, shape = CircleShape),
            colors = ButtonDefaults.buttonColors(containerColor = mossGreen)
        ) {
            Text(
                text = "Registrar",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = cinzelFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        if (state is RegisterState.Error) {
            AlertDialog(
                onDismissRequest = { viewModel.resetAlert() },
                confirmButton = {
                    Button(onClick = { viewModel.resetAlert() }) {
                        Text("Aceptar")
                    }
                },
                title = { Text(text = "Error") },
                text = { Text(text = (state as RegisterState.Error).message) }
            )
        }
    }
}