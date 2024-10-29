package com.app.quauhtlemallan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.data.model.Question
import com.app.quauhtlemallan.data.repository.QuestionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrueFalseGameViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex

    private val _correctAnswers = MutableStateFlow(0)
    val correctAnswers: StateFlow<Int> = _correctAnswers

    private val _timer = MutableStateFlow(15)
    val timer: StateFlow<Int> = _timer

    private var _gameEnded = MutableStateFlow(false)
    val gameEnded: StateFlow<Boolean> = _gameEnded

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer

    private var timerJob: Job? = null
    private var isPaused = false

    init {
        startTimer()
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val loadedQuestions = repository.getQuestionsToF()
            _questions.value = loadedQuestions
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _timer.value = 15
            while (_timer.value > 0 && !isPaused) {
                delay(1000)
                if (!isPaused) {
                    _timer.value -= 1
                }
            }
            if (_timer.value == 0 && !isPaused) {
                moveToNextQuestion()
            }
        }
    }

    fun delayNextQuestion() {
        viewModelScope.launch {
            delay(1000)
            moveToNextQuestion()
        }
    }

    fun pauseTimer() {
        isPaused = true
    }

    fun resumeTimer() {
        isPaused = false
        startTimer()
    }

    fun selectAnswer(answer: String): Boolean {
        _selectedAnswer.value = answer
        val isCorrect = answer == questions.value[_currentQuestionIndex.value].correcta

        if (isCorrect) {
            _correctAnswers.value += 1
        } else {
            pauseTimer()
        }

        return isCorrect
    }

    fun moveToNextQuestion() {
        _selectedAnswer.value = null
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value += 1
            startTimer()
        } else {
            _gameEnded.value = true
        }
    }

    fun resetGame() {
        _currentQuestionIndex.value = 0
        _correctAnswers.value = 0
        _gameEnded.value = false
        loadQuestions()
    }
}