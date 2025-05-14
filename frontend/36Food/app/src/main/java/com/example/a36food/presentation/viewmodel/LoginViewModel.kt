package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.network.NoConnectionException
import com.example.a36food.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkErrorHandler: NetworkErrorHandler,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    init {
        checkExistingToken()
    }

    private fun checkExistingToken() {
        viewModelScope.launch {
            val token = sharedPreferences.getString("access_token", null)
            android.util.Log.d("LoginViewModel", "Checking for existing token: ${token?.take(10)}")

            if (!token.isNullOrEmpty()) {
                // Show loading state
                _loginState.value = _loginState.value.copy(isLoading = true)

                try {
                    // Direct API call for better debugging
                    android.util.Log.d("LoginViewModel", "Verifying token...")
                    val isValid = authRepository.verifyToken(token)
                    android.util.Log.d("LoginViewModel", "Token verification result: $isValid")

                    if (isValid) {
                        _loginState.value = _loginState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    } else {
                        android.util.Log.d("LoginViewModel", "Token invalid, clearing")
                        _loginState.value = _loginState.value.copy(isLoading = false)
                        sharedPreferences.edit().remove("access_token").apply()
                    }
                } catch (e: NoConnectionException) {
                    android.util.Log.e("LoginViewModel", "No connection during token verification")
                    _onNetworkError?.invoke() // Show no connection screen
                    // Don't clear token or update login state - we'll retry when back online
                } catch (e: Exception) {
                    android.util.Log.e("LoginViewModel", "Exception during token verification", e)
                    _loginState.value = _loginState.value.copy(isLoading = false)

                    // Only remove token for authentication errors, not for timeouts/network issues
                    if (e.message?.contains("Unauthorized") == true ||
                        e.message?.contains("invalid") == true) {
                        sharedPreferences.edit().remove("access_token").apply()
                    }
                }
            }
        }
    }

    fun setNetworkErrorCallback(callback: () -> Unit) {
        _onNetworkError = callback
    }

    fun updateEmail(email: String) {
        _loginState.value = _loginState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _loginState.value = _loginState.value.copy(password = password)
    }

    fun login() {
        viewModelScope.launch {
            if (!isValidInput()) return@launch

            _loginState.value = _loginState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val result = networkErrorHandler.safeApiCall(
                    apiCall = {
                        authRepository.login(_loginState.value.email, _loginState.value.password)
                    },
                    onNetworkError = {
                        android.util.Log.d("LoginViewModel", "Network error during login")
                        _onNetworkError?.invoke()
                    }
                )

                _loginState.value = _loginState.value.copy(isLoading = false)

                result.fold(
                    onSuccess = { loginResponse ->
                        // Check if token is null or empty
                        val token = loginResponse.token
                        if (token.isNullOrEmpty()) {
                            _loginState.value = _loginState.value.copy(
                                errorMessage = "Server returned empty token"
                            )
                            return@fold
                        }

                        // Save token
                        sharedPreferences.edit().putString("access_token", token).apply()

                        _loginState.value = _loginState.value.copy(
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        // We already handled network errors, so this is for other errors
                        if (exception !is NoConnectionException) {
                            _loginState.value = _loginState.value.copy(
                                errorMessage = exception.message ?: "Đăng nhập không thành công"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                // Fallback error handling - should not reach here normally
                android.util.Log.e("LoginViewModel", "Unhandled exception", e)
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi không xác định: ${e.message}"
                )
            }
        }
    }

    private fun isValidInput(): Boolean {
        val email = _loginState.value.email
        val password = _loginState.value.password

        if (email.isBlank()) {
            _loginState.value = _loginState.value.copy(
                errorMessage = "Email không được để trống"
            )
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = _loginState.value.copy(
                errorMessage = "Email không hợp lệ"
            )
            return false
        }

        if (password.isBlank()) {
            _loginState.value = _loginState.value.copy(
                errorMessage = "Mật khẩu không được để trống"
            )
            return false
        }

        if (password.length < 6) {
            _loginState.value = _loginState.value.copy(
                errorMessage = "Mật khẩu phải có ít nhất 6 ký tự"
            )
            return false
        }

        return true
    }
}