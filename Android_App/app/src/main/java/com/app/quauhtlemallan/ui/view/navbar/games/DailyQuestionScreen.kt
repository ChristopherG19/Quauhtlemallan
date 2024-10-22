package com.app.quauhtlemallan.ui.view.navbar.games

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.ui.theme.crimsonRed
import com.app.quauhtlemallan.ui.theme.mossGreen
import com.app.quauhtlemallan.ui.viewmodel.DailyQuestionViewModel
import kotlinx.coroutines.delay

@Composable
fun DailyQuestionScreen(
    viewModel: DailyQuestionViewModel,
    navController: NavHostController,
    navigateBack: () -> Unit,
    navigateToNextQ: () -> Unit
) {
    val question = viewModel.dailyQuestion.value
    val hasAnswered = viewModel.hasAnswered.value
    val showDialog = viewModel.showDialog.value
    val isCorrectAnswer = viewModel.isCorrectAnswer.value
    val selectedOption = remember { mutableStateOf("") }
    val context = LocalContext.current
    var timeRemaining by remember { mutableLongStateOf(0L) }

    if (showDialog) {
        // Colores del diálogo
        val backgroundColor = if (isCorrectAnswer) Color(0xFFCCFF99) else Color(0xFFFFCCCC)
        val buttonColor = if (isCorrectAnswer) mossGreen else crimsonRed
        val messageText = if (isCorrectAnswer) "Sos muy pilas!" else "Uy, lo siento :("
        val messageColor = if (isCorrectAnswer) Color(0xFF4CAF50) else Color(0xFF8B0000)
        val buttonText = "Continuar"

        // Mostrar la respuesta correcta si fue incorrecto
        val correctAnswerText = if (!isCorrectAnswer) {
            "La respuesta correcta es: ${question?.correcta}"
        } else {
            ""
        }

        // Mostrar el diálogo como un Popup en la parte inferior
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                colors = CardColors(
                    containerColor = backgroundColor,
                    contentColor = Color.Black,
                    disabledContentColor = Color.Gray,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .wrapContentHeight()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Text(
                        text = messageText,
                        fontWeight = FontWeight.Bold,
                        color = messageColor,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isCorrectAnswer) {
                        Text(
                            text = correctAnswerText,
                            fontWeight = FontWeight.Normal,
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Button(
                        onClick = {
                            viewModel.closeDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = buttonText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    } else if (hasAnswered) {
        LaunchedEffect(hasAnswered) {
            if (hasAnswered) {
                val sharedPreferences = context.getSharedPreferences("dailyQuestionPrefs", Context.MODE_PRIVATE)
                val lastFetchTime = sharedPreferences.getLong("lastFetchTime", 0L)
                val currentTime = System.currentTimeMillis()
                val oneDayMillis = 24 * 60 * 60 * 1000

                timeRemaining = oneDayMillis - (currentTime - lastFetchTime)

                if (timeRemaining <= 0) {
                    timeRemaining = 0L
                    viewModel.closeDialog()

                    viewModel.resetDailyQuestion(context)
                    viewModel.loadDailyQuestion(context)
                    navigateToNextQ()
                } else {
                    while (timeRemaining > 0) {
                        delay(1000)
                        timeRemaining -= 1000

                        if (timeRemaining <= 0) {
                            timeRemaining = 0L
                            viewModel.closeDialog()

                            viewModel.resetDailyQuestion(context)
                            viewModel.loadDailyQuestion(context)
                            navigateToNextQ()
                            break
                        }
                    }
                }

            }
        }

        var hours = (timeRemaining / (1000 * 60 * 60)) % 24
        var minutes = (timeRemaining / (1000 * 60)) % 60
        var seconds = (timeRemaining / 1000) % 60


        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ya has respondido la pregunta de hoy.",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = cinzelFontFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tiempo restante para la próxima pregunta:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = cinzelFontFamily,
                )

                Text(
                    text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = cinzelFontFamily,
                    textAlign = TextAlign.Center
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_quit),
                contentDescription = "Regresar",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(24.dp)
                    .clickable {
                        navigateBack()
                    }
            )
        }
    } else {
        LaunchedEffect(Unit) {
            Log.d("DailyQuestionScreen", "Llamando a loadDailyQuestion()")
            viewModel.loadDailyQuestion(context)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_quit),
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

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar la pregunta
            question?.let { q ->
                Text(
                    text = q.pregunta,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = cinzelFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Opciones de respuesta
                q.respuestas.forEach { option ->
                    AnswerButton(
                        text = option,
                        isSelected = selectedOption.value == option,
                        onClick = {
                            selectedOption.value = option
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Botón de enviar respuesta
                Button(
                    onClick = {
                        viewModel.submitAnswer(context, selectedOption.value == q.correcta)
                    },
                    enabled = selectedOption.value.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mossGreen)
                ) {
                    Text(
                        text = "Enviar respuesta",
                        fontSize = 16.sp,
                        fontFamily = cinzelFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            } ?: Text(
                text = "Cargando pregunta...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = cinzelFontFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun AnswerButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF42A5F5) else Color.White
        ),
        border = BorderStroke(2.dp, if (isSelected) Color(0xFF42A5F5) else Color.Gray),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp) // Altura del botón
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
