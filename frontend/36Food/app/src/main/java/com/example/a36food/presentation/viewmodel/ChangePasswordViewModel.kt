package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.dto.ChangePasswordRequest
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePasswordState (
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val networkErrorHandler: NetworkErrorHandler,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    fun setNetworkErrorCallback(callback: () -> Unit) {
        _onNetworkError = callback
    }

    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "All fields are required"
            )
            return
        }

        if (newPassword != confirmPassword) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "New passwords don't match"
            )
            return
        }

        if (newPassword.length < 6) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "New password must be at least 6 characters"
            )
            return
        }

        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            val token = sharedPreferences.getString("access_token", null)?.trim()
            if (token.isNullOrEmpty()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Not logged in"
                )
                return@launch
            }

            val authHeader = if (!token.startsWith("Bearer ")) "Bearer $token" else token
            val request = ChangePasswordRequest(oldPassword, newPassword)

            Log.d("ChangePasswordVM", "Sending request with old/new password lengths: ${oldPassword.length}/${newPassword.length}")

            val result = networkErrorHandler.safeApiCall(
                apiCall = { userRepository.changePassword(authHeader, request) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    val errorMessage = when {
                        exception.message?.contains("Mật khẩu cũ không chính xác") == true ->
                            "Mật khẩu hiện tại không chính xác"
                        exception.message?.contains("không được để trống") == true ->
                            "Vui lòng điền đầy đủ thông tin"
                        exception.message?.contains("401") == true ->
                            "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại"
                        exception.message?.contains("404") == true ->
                            "Không tìm thấy thông tin người dùng"
                        else -> exception.message ?: "Đã xảy ra lỗi, vui lòng thử lại sau"
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            )
        }
    }

    fun clearState() {
        _state.value = ChangePasswordState()
    }
}