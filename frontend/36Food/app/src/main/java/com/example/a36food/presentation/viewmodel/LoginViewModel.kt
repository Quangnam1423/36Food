package com.example.a36food.presentation.viewmodel

import com.example.a36food.domain.model.User
import androidx.lifecycle.ViewModel
import com.example.a36food.data.remote.AuthApiService
import com.example.a36food.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class LoginUiState {
    object Idle: LoginUiState()
    object Loading: LoginUiState()
    data class Success(val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel(){

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    // check login
    fun checkLogin(userName: String, password: String) {
        //return authRepository.loginUser(userName, password)
    }
}