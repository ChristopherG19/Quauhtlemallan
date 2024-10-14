package com.app.quauhtlemallan.ui.view.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.ui.theme.FacebookBack
import com.app.quauhtlemallan.ui.theme.SelectedField
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.ui.theme.crimsonRed
import com.app.quauhtlemallan.ui.theme.mossGreen
import com.app.quauhtlemallan.data.model.User
import com.app.quauhtlemallan.ui.viewmodel.LoginViewModel
import com.app.quauhtlemallan.util.LoginState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navigateToHome: (User) -> Unit,
    navigateBack: () -> Unit,
    googleSignInClient: GoogleSignInClient
) {

    val loginState by viewModel.loginStateFlow.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Google Sign-In launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken) { user ->
                        navigateToHome(user)
                    }
                } else {
                    viewModel.resetAlert()
                    viewModel._loginState.value = LoginState.Error("No se pudo obtener el token de Google.")
                }
            } catch (e: ApiException) {
                viewModel.resetAlert()
                viewModel._loginState.value = LoginState.Error("Error en Google Sign-In: ${e.message}")
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
                    .clickable{
                        navigateBack()
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
            value = viewModel.email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = SelectedField
            ),
            placeholder = { Text(text = "Correo", color = Color.Black) }
        )

        Spacer(Modifier.height(16.dp))
        TextField(
            value = viewModel.password,
            onValueChange = viewModel::onPasswordChange,
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
                    Icon(painter = image, contentDescription = "Toggle password visibility")
                }
            },
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.signInWithEmailAndPassword { user ->
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
                text = "Iniciar Sesión",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        when (loginState) {
            is LoginState.Loading -> {
                CircularProgressIndicator()
            }
            is LoginState.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetAlert() },
                    confirmButton = {
                        Button(onClick = { viewModel.resetAlert() }) {
                            Text("Aceptar")
                        }
                    },
                    title = { Text(text = "Error") },
                    text = { Text(text = (loginState as LoginState.Error).message) }
                )
            }
            else -> {}
        }

        Spacer(Modifier.height(48.dp))

        CustomButton(
            modifier = Modifier.clickable {
                launcher.launch(googleSignInClient.signInIntent)
            },
            painter = painterResource(id = R.drawable.google),
            title = "Iniciar con Google",
            color = crimsonRed
        )

        Spacer(Modifier.height(12.dp))

        CustomButton(
            modifier = Modifier.clickable {

            },
            painter = painterResource(id = R.drawable.facebook),
            title = "Iniciar con Facebook",
            color = FacebookBack
        )
    }
}

@Composable
fun CustomButton(modifier: Modifier, painter: Painter, title: String, color: Color) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(color, shape = CircleShape)
            .border(2.dp, color, CircleShape),
        contentAlignment = Alignment.CenterStart
    ) {

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 18.dp)
                .size(16.dp)
        )
        Text(
            text = title,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}
