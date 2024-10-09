package com.app.quauhtlemallan.presentation.login

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
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.ui.theme.FacebookBack
import com.app.quauhtlemallan.ui.theme.SelectedField
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.ui.theme.crimsonRed
import com.app.quauhtlemallan.ui.theme.mossGreen
import com.app.quauhtlemallan.viewmodels.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    auth: FirebaseAuth,
    viewModel: LoginViewModel,
    navigateToHome: () -> Unit,
    navigateBack: () -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
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
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.signInWithEmailAndPassword(auth, navigateToHome)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = mossGreen)
        ) {
            Text(text = "Iniciar Sesión", color = Color.White)
        }

        if (viewModel.showEmptyFieldsAlert) {
            AlertDialog(
                onDismissRequest = { viewModel.resetAlert() },
                confirmButton = {
                    Button(onClick = { viewModel.resetAlert() }) {
                        Text("Aceptar")
                    }
                },
                title = { Text(text = "Error") },
                text = { Text(text = "Por favor, completa todos los campos.") }
            )
        }

        Spacer(Modifier.height(48.dp))

        CustomButton(
            modifier = Modifier.clickable {
                onGoogleSignInClick()
            },
            painter = painterResource(id = R.drawable.google),
            title = "Iniciar con Google",
            color = crimsonRed
        )

        Spacer(Modifier.height(8.dp))

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
            .height(40.dp)
            .background(color, shape = CircleShape)
            .border(2.dp, color, CircleShape),
        contentAlignment = Alignment.CenterStart
    ) {

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp)
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
