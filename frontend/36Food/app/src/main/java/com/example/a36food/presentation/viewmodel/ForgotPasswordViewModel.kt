package com.example.a36food.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class ForgotPasswordUiState {
    data class InputEmail(
        val email: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,

    ) : ForgotPasswordUiState()

    data class VerifyCode(
        val email: String = "",
        val code: String = "",
        val newPassword: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : ForgotPasswordUiState()

    data class ChangePassWord(
        val password: String? = null,
        val newPassword: String? = null
    ) : ForgotPasswordUiState()
}

class ForgotPasswordViewModel : ViewModel() {
    /*var email by mutableStateOf("")
    var verificationCode by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isCodeSent by mutableStateOf(false)
    var isPasswordReset by mutableStateOf(false)

    private val coroutineScope = viewModelScope
     */

    var uiState by mutableStateOf<ForgotPasswordUiState>(
        ForgotPasswordUiState.InputEmail()
    )
        private set

    fun onEmailChanged(newEmail: String) {
        if (uiState is ForgotPasswordUiState.InputEmail) {
            uiState = (uiState as ForgotPasswordUiState.InputEmail).copy(email = newEmail)
        }
    }

    fun onSendCodeClicked() {
        if (uiState is ForgotPasswordUiState.InputEmail) {
            val email = (uiState as ForgotPasswordUiState.InputEmail).email

            uiState = ForgotPasswordUiState.InputEmail(email, isLoading = true)

            viewModelScope.launch {
                delay(1000)
                uiState = ForgotPasswordUiState.VerifyCode(email = email)
            }
        }
    }

    fun onCodeChanged(code: String) {
        if (uiState is ForgotPasswordUiState.VerifyCode) {
            uiState = (uiState as ForgotPasswordUiState.VerifyCode).copy(code = code)
        }
    }

    fun onPasswordChanged(password: String) {
        if (uiState is ForgotPasswordUiState.VerifyCode) {
            uiState = (uiState as ForgotPasswordUiState.VerifyCode).copy(newPassword = password)
        }
    }

    fun onResetPasswordClicked() {
        if (uiState is ForgotPasswordUiState.VerifyCode) {
            val state = uiState as ForgotPasswordUiState.VerifyCode
            uiState = state.copy(isLoading = true)

            viewModelScope.launch {
                delay(1000)
            }
        }
    }
}