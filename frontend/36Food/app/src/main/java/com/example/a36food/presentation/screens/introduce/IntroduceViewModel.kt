package com.example.a36food.presentation.screens.introduce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.local.UserPreferences
import com.example.a36food.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroduceViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(IntroduceState())
    val state: StateFlow<IntroduceState> = _state.asStateFlow()
    val refreshToken: String = userPreferences.getRefreshToken().toString()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val refreshToken = userPreferences.getRefreshToken()

            if (refreshToken != null && userRepository.hasValidSession()) {
                userRepository.refreshToken(refreshToken)
                    .onSuccess {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                shouldNavigateToHome = true
                            )
                        }
                    }
                    .onFailure { exception ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                shouldNavigateToLogin = true,
                                error = exception.message
                            )
                        }
                    }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        shouldNavigateToLogin = true
                    )
                }
            }
        }
    }

    fun resetNavigation() {
        _state.update {
            it.copy(shouldNavigateToLogin = false)
        }
    }
}