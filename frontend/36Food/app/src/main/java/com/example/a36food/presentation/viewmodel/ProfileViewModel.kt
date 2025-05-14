package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.UserRepository
import com.example.a36food.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel  @Inject constructor(
    private val userRepository: UserRepository,
    private val networkErrorHandler: NetworkErrorHandler,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    init {
        loadUserProfile()
    }

    fun setNetworkErrorCallback(callback: () ->Unit) {
        _onNetworkError = callback
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)

            val token = sharedPreferences.getString("token", null)
            if (token.isNullOrEmpty()) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = "Token not found",
                    isLoggedOut = true
                )
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = {userRepository.getCurrentUser(token)},
                onNetworkError = {
                    _onNetworkError?.invoke()
                }
            )

            result.fold(
                onSuccess = { user ->
                    _profileState.value = _profileState.value.copy(
                        isLoading = false,
                        user = user,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _profileState.value = _profileState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )

                    if (exception.message?.contains("Unauthorized") == true) {
                        logout()
                    }
                }
            )
        }
    }

    fun logout () {
        viewModelScope.launch {
            sharedPreferences.edit().remove("access_token").apply()
            _profileState.value = _profileState.value.copy(
                isLoggedOut = true,
                user = null
            )
        }
    }

    fun formatJoinDate(dateString: String?): String {
        if (dateString == null) return "N/A"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: "N/A"
        } catch (e: Exception) {
            dateString
        }
    }

    fun clearError() {
        _profileState.value = _profileState.value.copy(error = null)
    }
}