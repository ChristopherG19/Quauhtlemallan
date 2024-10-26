package com.app.quauhtlemallan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.quauhtlemallan.data.repository.QuestionRepository
import com.app.quauhtlemallan.data.repository.UserRepository
import com.app.quauhtlemallan.ui.viewmodel.AchievementsViewModel
import com.app.quauhtlemallan.ui.viewmodel.ChatViewModel
import com.app.quauhtlemallan.ui.viewmodel.DailyQuestionViewModel
import com.app.quauhtlemallan.ui.viewmodel.LoginViewModel
import com.app.quauhtlemallan.ui.viewmodel.ProgressViewModel
import com.app.quauhtlemallan.ui.viewmodel.RegisterViewModel
import com.app.quauhtlemallan.ui.viewmodel.SettingsViewModel
import com.app.quauhtlemallan.ui.viewmodel.TimeQuestionViewModel
import com.app.quauhtlemallan.ui.viewmodel.TrueFalseGameViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModelFactory(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(auth, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RegisterViewModelFactory(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(auth, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SettingsViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProgressViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            return ProgressViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AchievementsViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementsViewModel::class.java)) {
            return AchievementsViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ChatViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DailyQuestionViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyQuestionViewModel::class.java)) {
            return DailyQuestionViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TrueFalseGameViewModelFactory(
    private val questionRepository: QuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrueFalseGameViewModel::class.java)) {
            return TrueFalseGameViewModel(questionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TimeQuestionViewModelFactory(
    private val repository: QuestionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeQuestionViewModel::class.java)) {
            return TimeQuestionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



