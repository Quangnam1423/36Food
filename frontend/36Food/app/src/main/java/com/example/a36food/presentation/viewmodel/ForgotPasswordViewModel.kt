package com.example.a36food.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

sealed class ForgotPasswordUiState()

class ForgotPasswordViewModel : ViewModel() {
    var email by mutableStateOf("")
    var verificationCode by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isCodeSent by mutableStateOf(false)
    var isPasswordReset by mutableStateOf(false)

    private val coroutineScope = viewModelScope
}