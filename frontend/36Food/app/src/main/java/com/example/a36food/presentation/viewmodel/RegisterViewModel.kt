package com.example.a36food.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf


import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel() : ViewModel() {
    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    fun onUsernameChanged(value: String) {
        _registerState.value = _registerState.value.copy(username = value)
    }

    fun onEmailChanged(value: String) {
        _registerState.value = _registerState.value.copy(email = value)
    }

    fun onPhoneNumberChanged(value: String) {
        _registerState.value = _registerState.value.copy(phoneNumber = value)
    }

    fun onPasswordChanged(value: String) {
        _registerState.value = _registerState.value.copy(password = value)
    }

    fun onConfirmPasswordChanged(value: String) {
        _registerState.value = _registerState.value.copy(confirmPassword = value)
    }

    fun checkConfirmPassword(): Boolean {
        if (_registerState.value.password.equals(_registerState.value.confirmPassword))
            return true;
        else
            return false;
    }

    fun registerUser() {
        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(isLoading = true, errorMessage = null)

            if (checkConfirmPassword()) {
                _registerState.value = _registerState.value.copy(errorMessage = "Mật khẩu không khớp!", isLoading = false)
            }

            _registerState.value = _registerState.value.copy(isLoading = false)
        }
    }
}