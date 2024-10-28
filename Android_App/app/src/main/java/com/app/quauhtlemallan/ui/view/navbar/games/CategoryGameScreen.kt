package com.app.quauhtlemallan.ui.view.navbar.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.ui.theme.cinzelFontFamily
import com.app.quauhtlemallan.ui.viewmodel.CategoryGameViewModel

@Composable
fun CategoryGameScreen(
    viewModel: CategoryGameViewModel,
    categoryId: String,
    navController: NavHostController,
    navigateBack: () -> Unit
) {

    LaunchedEffect(categoryId) {
        viewModel.loadQuestions(categoryId)
    }
    val questions by viewModel.questions.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val timer by viewModel.timer.collectAsState()
    val correctAnswers by viewModel.correctAnswers.collectAsState()
    val gameEnded by viewModel.gameEnded.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val answerColors by viewModel.answerColors.collectAsState()

    val question = questions.getOrNull(currentQuestionIndex)

    if (gameEnded) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Respuestas correctas: $correctAnswers / ${questions.size}",
                fontFamily = cinzelFontFamily,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navigateBack() }) {
                Text("Volver a categorías", fontFamily = cinzelFontFamily, fontWeight = FontWeight.Normal)
            }
        }
    } else {
        if (questions.isNotEmpty()) {
            val currentQuestion = questions[currentQuestionIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_quit),
                        contentDescription = "Regresar",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.navigateUp()
                            }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = timer / 15f,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp),
                            color = Color(0xFF4CAF50) // Verde
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "${timer}s",
                            fontSize = 16.sp,
                            fontFamily = cinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (currentQuestion.tieneImagen && currentQuestion.imagenUrl != null) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .border(2.5.dp, Color.Black, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .aspectRatio(16f / 9f)
                    ) {
                        Image(
                            painter = rememberImagePainter(data = currentQuestion.imagenUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Text(
                    text = currentQuestion.pregunta,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = cinzelFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                question?.let {

                    it.respuestas.forEachIndexed { index, option ->
                        AnswerButtonCategoryTime(
                            text = option,
                            backgroundColor = answerColors[index],
                            onClick = {
                                viewModel.selectAnswer(option)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                } ?: Text(
                    text = "Cargando pregunta...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = cinzelFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${currentQuestionIndex + 1} / ${questions.size}",
                    fontSize = 18.sp,
                    fontFamily = cinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Black
                )
            }

        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun AnswerButtonCategoryTime(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = cinzelFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}