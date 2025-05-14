package com.example.a36food.presentation.viewmodel


import android.content.SharedPreferences
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.dto.RegisterRequest
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.network.NoConnectionException
import com.example.a36food.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class RegisterState(
    val name: String = "",
    val nameError: String? = null,

    val email: String = "",
    val emailError: String? = null,

    val phone: String = "",
    val phoneError: String? = null,

    val gender: String = "",

    val dob: String = "",
    val dobError: String? = null,

    val password: String = "",
    val passwordError: String? = null,

    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkErrorHandler: NetworkErrorHandler,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    fun setNetworkErrorCallback(callback: () -> Unit) {
        _onNetworkError = callback
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun register() {
        viewModelScope.launch {
            if (!isFormValid()) return@launch

            _registerState.value = _registerState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val userSlug = _registerState.value.name
                .lowercase()
                .replace("\\s+".toRegex(), "-")
                .replace("[^a-z0-9-]".toRegex(), "")

            val formattedDob = if (_registerState.value.dob.isNotEmpty() &&
                _registerState.value.dob.count { it == '/' } == 2) {
                formatDobForApi(_registerState.value.dob)
            } else null

            android.util.Log.d("RegisterViewModel", "Original DOB: ${_registerState.value.dob}")
            android.util.Log.d("RegisterViewModel", "Formatted DOB for API: $formattedDob")

            val registerRequest = RegisterRequest(
                userEmail = _registerState.value.email,
                userName = _registerState.value.name,
                userPassword = _registerState.value.password,
                userPhone = _registerState.value.phone,
                userGender = _registerState.value.gender.ifEmpty { null },
                userDob = formattedDob,
                userSlug = userSlug,
                roleId = 2 // Regular user role
            )

            try {
                val result = networkErrorHandler.safeApiCall(
                    apiCall = { authRepository.register(registerRequest) },
                    onNetworkError = { _onNetworkError?.invoke() }
                )

                _registerState.value = _registerState.value.copy(isLoading = false)

                result.fold(
                    onSuccess = { response ->
                        // Save token to SharedPreferences
                        response.token?.let { token ->
                            android.util.Log.d("RegisterViewModel", "Saving token: ${token.take(10)}...")
                            sharedPreferences.edit()
                                .putString("access_token", token)
                                .apply()

                            // Automatically login with the new token
                            loginWithToken(token)
                        } ?: run {
                            // If no token in response, just mark registration as successful
                            _registerState.value = _registerState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        if (exception !is NoConnectionException) {
                            _registerState.value = _registerState.value.copy(
                                errorMessage = exception.message
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    private fun loginWithToken(token: String) {
        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(isLoading = true)
            android.util.Log.d("RegisterViewModel", "Attempting login with token after registration")

            try {
                val result = networkErrorHandler.safeApiCall(
                    apiCall = { authRepository.loginWithToken(token) },
                    onNetworkError = { _onNetworkError?.invoke() }
                )

                result.fold(
                    onSuccess = { loginResponse ->
                        android.util.Log.d("RegisterViewModel", "Token login successful after registration")

                        // Save the token
                        loginResponse.token?.let { newToken : String->
                            sharedPreferences.edit().putString("access_token", newToken).apply()
                            android.util.Log.d("RegisterViewModel", "Token saved to SharedPreferences: ${newToken.take(10)}...")

                            // Verify token was saved
                            val savedToken = sharedPreferences.getString("access_token", null)
                            android.util.Log.d("RegisterViewModel", "Verification - Token exists in SharedPreferences: ${!savedToken.isNullOrEmpty()}")
                            if (!savedToken.isNullOrEmpty()) {
                                android.util.Log.d("RegisterViewModel", "Verification - Saved token starts with: ${savedToken.take(10)}...")
                            }
                        }

                        _registerState.value = _registerState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        android.util.Log.e("RegisterViewModel", "Token login failed: ${exception.message}")
                        _registerState.value = _registerState.value.copy(
                            isLoading = false,
                            errorMessage = "Đăng nhập tự động thất bại: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("RegisterViewModel", "Exception during token login", e)
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi đăng nhập: ${e.message}"
                )
            }
        }
    }

    // Add this function to check if user is logged in
    fun isUserLoggedIn(): Boolean {
        val token = sharedPreferences.getString("access_token", null)
        return !token.isNullOrEmpty()
    }

    // Add this to clear registration state
    fun clearRegistrationState() {
        _registerState.value = RegisterState()
    }


    fun updateName(name: String) {
        _registerState.value = _registerState.value.copy(
            name = name,
            nameError = validateName(name)
        )
    }

    fun updateEmail(email: String) {
        _registerState.value = _registerState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun updatePhone(phone: String) {
        _registerState.value = _registerState.value.copy(
            phone = phone,
            phoneError = validatePhone(phone)
        )
    }

    fun updateGender(gender: String) {
        _registerState.value = _registerState.value.copy(gender = gender)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDob(dob: String) {
        // If user is deleting, just update the value
        if (dob.length < _registerState.value.dob.length) {
            _registerState.value = _registerState.value.copy(
                dob = dob,
                dobError = null
            )
            return
        }

        // Remove any non-digit characters from the input
        val cleanInput = dob.filter { it.isDigit() }.take(8)

        // Format with / separators
        val formatted = when {
            cleanInput.length >= 4 -> {
                val day = cleanInput.substring(0, 2)
                val month = cleanInput.substring(2, 4)
                val year = if (cleanInput.length > 4) cleanInput.substring(4) else ""
                if (year.isEmpty()) "$day/$month" else "$day/$month/$year"
            }
            cleanInput.length >= 2 -> {
                val day = cleanInput.substring(0, 2)
                val month = if (cleanInput.length > 2) cleanInput.substring(2) else ""
                if (month.isEmpty()) day else "$day/$month"
            }
            else -> cleanInput
        }

        // Only validate complete dates
        val error = if (cleanInput.length == 8) validateDob(formatted) else null

        _registerState.value = _registerState.value.copy(
            dob = formatted,
            dobError = error
        )
    }

    fun updatePassword(password: String) {
        val state = _registerState.value
        _registerState.value = state.copy(
            password = password,
            passwordError = validatePassword(password),
            confirmPasswordError = if (state.confirmPassword.isNotEmpty())
                validatePasswordMatch(password, state.confirmPassword) else null
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _registerState.value = _registerState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validatePasswordMatch(_registerState.value.password, confirmPassword)
        )
    }

    fun isFormValid(): Boolean {
        val state = _registerState.value
        return state.name.isNotEmpty() && state.nameError == null &&
                state.email.isNotEmpty() && state.emailError == null &&
                state.phone.isNotEmpty() && state.phoneError == null &&
                state.password.isNotEmpty() && state.passwordError == null &&
                state.confirmPassword.isNotEmpty() && state.confirmPasswordError == null
    }

    // Validation functions
    private fun validateName(name: String): String? {
        return when {
            name.isEmpty() -> "Họ tên không được để trống"
            name.length < 2 -> "Họ tên phải có ít nhất 2 ký tự"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email không được để trống"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ"
            else -> null
        }
    }

    private fun validatePhone(phone: String): String? {
        return when {
            phone.isEmpty() -> "Số điện thoại không được để trống"
            !Regex("^0[0-9]{9}$").matches(phone) -> "Số điện thoại không hợp lệ (phải có 10 số và bắt đầu bằng số 0)"
            else -> null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateDob(dob: String): String? {
        try {
            val parts = dob.split("/")
            if (parts.size != 3) {
                return "Ngày sinh phải có định dạng DD/MM/YYYY"
            }

            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            if (year < 1900 || year > LocalDateTime.now().year) {
                return "Năm sinh không hợp lệ"
            }
            if (month < 1 || month > 12) {
                return "Tháng không hợp lệ"
            }

            val daysInMonth = when (month) {
                2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
            }

            if (day < 1 || day > daysInMonth) {
                return "Ngày không hợp lệ cho tháng đã chọn"
            }

            return null
        } catch (e: Exception) {
            return "Ngày sinh không hợp lệ"
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDobForApi(dob: String): String {
        try {
            val parts = dob.split("/")
            if (parts.size != 3) return ""

            val day = parts[0].padStart(2, '0')
            val month = parts[1].padStart(2, '0')
            val year = parts[2].padStart(4, '0')

            // Format as YYYY-MM-DDT00:00:00 for API - exact format for LocalDateTime.parse()
            return "${year}-${month}-${day}T00:00:00"
        } catch (e: Exception) {
            android.util.Log.e("RegisterViewModel", "Error formatting DOB for API", e)
            return ""
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Mật khẩu không được để trống"
            password.length < 6 -> "Mật khẩu phải có ít nhất 6 ký tự"
            !password.any { it.isDigit() } -> "Mật khẩu phải chứa ít nhất 1 chữ số"
            !password.any { it.isLetter() } -> "Mật khẩu phải chứa ít nhất 1 chữ cái"
            else -> null
        }
    }

    private fun validatePasswordMatch(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Vui lòng xác nhận mật khẩu"
            password != confirmPassword -> "Mật khẩu không khớp"
            else -> null
        }
    }
}