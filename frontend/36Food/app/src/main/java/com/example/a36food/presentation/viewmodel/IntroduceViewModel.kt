package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IntroduceState(
    val isFirstLaunch: Boolean = true,
    val hasValidToken: Boolean = false
)

@HiltViewModel
class IntroduceViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(IntroduceState())
    val state: StateFlow<IntroduceState> = _state

    private val PREF_FIRST_LAUNCH = "pref_first_launch"

    fun checkFirstLaunch() {
        viewModelScope.launch {
            // Check if this is the first launch
            val isFirstLaunch = sharedPreferences.getBoolean(PREF_FIRST_LAUNCH, true)

            // Check if we have a valid token
            val token = sharedPreferences.getString("access_token", null)
            var hasValidToken = false

            if (!token.isNullOrEmpty()) {
                try {
                    hasValidToken = authRepository.verifyToken(token)
                } catch (e: Exception) {
                    // Token verification failed, token is invalid
                    sharedPreferences.edit().remove("access_token").apply()
                }
            }

            _state.update { it.copy(
                isFirstLaunch = isFirstLaunch,
                hasValidToken = hasValidToken
            )}
        }
    }

    fun setFirstLaunchComplete() {
        sharedPreferences.edit().putBoolean(PREF_FIRST_LAUNCH, false).apply()
        _state.update { it.copy(isFirstLaunch = false) }
    }
}