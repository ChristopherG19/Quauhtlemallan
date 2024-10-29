package com.app.quauhtlemallan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.data.model.Question
import com.app.quauhtlemallan.data.repository.QuestionRepository
import com.app.quauhtlemallan.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrueFalseGameViewModel(
    private val repository: QuestionRepository,
    private val userRepository: UserRepository
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
    private val _tempScore = MutableStateFlow(0)
    private val _badgePoints = mutableMapOf<String, Int>()

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
        val currentQuestion = questions.value[_currentQuestionIndex.value]
        val isCorrect = answer == currentQuestion.correcta

        if (isCorrect) {
            _correctAnswers.value += 1
            _tempScore.value += currentQuestion.puntos
            currentQuestion.insignias.forEach { badgeId ->
                val currentBadgeScore = _badgePoints[badgeId] ?: 0
                _badgePoints[badgeId] = currentBadgeScore + currentQuestion.puntos
            }

        } else {
            pauseTimer()
        }

        return isCorrect
    }

    private fun updateScoreForCorrectAnswer(question: Question) {
        viewModelScope.launch {
            userRepository.addPointsToUserScore(question.puntos)
            question.insignias.forEach { badgeId ->
                userRepository.addPointsToBadge(badgeId, question.puntos)
            }
        }
    }

    fun moveToNextQuestion() {
        _selectedAnswer.value = null
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value += 1
            startTimer()
        } else {
            _gameEnded.value = true
            finalizeScore()
        }
    }

    private fun finalizeScore() {
        viewModelScope.launch {
            userRepository.addPointsToUserScore(_tempScore.value)
            _badgePoints.forEach { (badgeId, points) ->
                userRepository.addPointsToBadge(badgeId, points)
            }
            _tempScore.value = 0
            _badgePoints.clear()
        }
    }

    fun resetGame() {
        _currentQuestionIndex.value = 0
        _correctAnswers.value = 0
        _gameEnded.value = false
        loadQuestions()
    }
}