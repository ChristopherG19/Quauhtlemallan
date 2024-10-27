package com.app.quauhtlemallan.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.data.model.Question
import com.app.quauhtlemallan.data.repository.QuestionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryGameViewModel(
    private val repository: QuestionRepository
) : ViewModel(){

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex

    private val _correctAnswers = MutableStateFlow(0)
    val correctAnswers: StateFlow<Int> = _correctAnswers

    private val _timer = MutableStateFlow(15)
    val timer: StateFlow<Int> = _timer

    private val _gameEnded = MutableStateFlow(false)
    val gameEnded: StateFlow<Boolean> = _gameEnded

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer

    private val _isCorrectAnswer = MutableStateFlow(false)
    val isCorrectAnswer: StateFlow<Boolean> = _isCorrectAnswer

    private val _answerColors = MutableStateFlow<List<Color>>(listOf(Color(0xFF29395E), Color(0xFF29395E), Color(0xFF29395E), Color(0xFF29395E)))
    val answerColors: StateFlow<List<Color>> = _answerColors

    private var timerJob: Job? = null

    fun loadQuestions(id:String) {
        viewModelScope.launch {
            val loadedQuestions = repository.getQuestionsByCategory(id)
            _questions.value = loadedQuestions
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _timer.value = 15
            while (_timer.value > 0) {
                delay(1000)
                _timer.value -= 1
            }
            if (_timer.value == 0) {
                moveToNextQuestion()
            }
        }
    }

    fun selectAnswer(answer: String): Boolean {
        _selectedAnswer.value = answer

        val currentQuestion = questions.value[_currentQuestionIndex.value]
        val isCorrect = answer == currentQuestion.correcta
        _isCorrectAnswer.value = isCorrect

        val colors = currentQuestion.respuestas.map { option ->
            when {
                option == currentQuestion.correcta -> Color(0xFF4CAF50)
                option == answer -> Color(0xFFF44336)
                else -> Color.LightGray
            }
        }
        _answerColors.value = colors

        if (isCorrect) {
            _correctAnswers.value += 1
        }
        viewModelScope.launch {
            delay(1000)
            moveToNextQuestion()
        }

        return isCorrect

    }

    private fun moveToNextQuestion() {
        _selectedAnswer.value = null
        _isCorrectAnswer.value = false
        _answerColors.value = listOf(Color(0xFF29395E), Color(0xFF29395E), Color(0xFF29395E), Color(0xFF29395E))

        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value += 1
            startTimer()
        } else {
            _gameEnded.value = true
            timerJob?.cancel()
        }
    }

}