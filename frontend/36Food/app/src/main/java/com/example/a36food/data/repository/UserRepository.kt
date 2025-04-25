package com.example.a36food.data.repository

import com.example.a36food.data.local.UserPreferences
import com.example.a36food.data.network.base.ApiResponse
import com.example.a36food.data.network.base.BaseRepository
import com.example.a36food.data.network.services.UserService
import com.example.a36food.domain.model.requests.RegisterRequest
import com.example.a36food.domain.model.User
import com.example.a36food.domain.model.requests.ChangePasswordRequest
import com.example.a36food.domain.model.requests.ForgotPasswordRequest
import com.example.a36food.domain.model.requests.LoginWithCredentialsRequest
import com.example.a36food.domain.model.requests.RefeshTokenRequest
import com.example.a36food.domain.model.requests.ResetPasswordRequest
import com.example.a36food.domain.model.requests.VerifyCodeRequest
import com.example.a36food.domain.model.responses.TokenResponse
import javax.inject.Inject


interface UserRepository {
    suspend fun loginWithCredentials(email: String, password: String): Result<TokenResponse>
    suspend fun loginWithToken(token: String): Result<TokenResponse>
    suspend fun register(request: RegisterRequest): Result<TokenResponse>
    suspend fun refreshToken(refreshToken: String): Result<TokenResponse>
    suspend fun forgotPassword(request: ForgotPasswordRequest): Result<Unit>
    suspend fun verifyCode(request: VerifyCodeRequest): Result<Unit>
    suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit>
    suspend fun changePassword(request: ChangePasswordRequest): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
    suspend fun logout(): Result<Unit>
    fun hasValidSession(): Boolean
}

class UserRepositoryImpl @Inject constructor(
    private val api: UserService,
    private val userPreferences: UserPreferences
) : UserRepository, BaseRepository() {

    override suspend fun loginWithCredentials(email: String, password: String): Result<TokenResponse> {
        return when (val response = safeApiCall {
            api.loginWithCredentials(LoginWithCredentialsRequest(email, password))
        }) {
            is ApiResponse.Success -> {
                val tokenResponse = response.data
                userPreferences.saveTokens(tokenResponse)
                Result.success(tokenResponse)
            }
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override suspend fun loginWithToken(token: String): Result<TokenResponse> {
        return when (val response = safeApiCall {
            api.loginWithToken("Bearer $token")
        }) {
            is ApiResponse.Success -> {
                val tokenResponse = response.data
                userPreferences.saveTokens(tokenResponse)
                Result.success(tokenResponse)
            }
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override suspend fun register(request: RegisterRequest): Result<TokenResponse> {
        return when (val response = safeApiCall { api.register(request) }) {
            is ApiResponse.Success -> {
                val tokenResponse = response.data
                userPreferences.saveTokens(tokenResponse)
                Result.success(tokenResponse)
            }
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<TokenResponse> {
        return when (val response = safeApiCall {
            api.refreshToken(RefeshTokenRequest(refreshToken))
        }) {
            is ApiResponse.Success -> {
                val tokenResponse = response.data
                userPreferences.saveTokens(tokenResponse)
                Result.success(tokenResponse)
            }
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override suspend fun forgotPassword(request: ForgotPasswordRequest): Result<Unit> {
        return when (val response = safeApiCall { api.forgotPassword(request) }) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override suspend fun verifyCode(request: VerifyCodeRequest): Result<Unit> {
        return when (val response = safeApiCall { api.verifyCode(request) }) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
        return when (val response = safeApiCall { api.resetPassword(request) }) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override suspend fun changePassword(request: ChangePasswordRequest): Result<Unit> {
        return when (val response = safeApiCall {
            api.changePassword(userPreferences.getAccessToken() ?: "", request)
        }) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return when (val response = safeApiCall {
            api.getCurrentUser(userPreferences.getAccessToken() ?: "")
        }) {
            is ApiResponse.Success -> Result.success(response.data)
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return when (val response = safeApiCall {
            api.logout(userPreferences.getAccessToken() ?: "")
        }) {
            is ApiResponse.Success -> {
                userPreferences.clearTokens()
                Result.success(Unit)
            }
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading state not handled"))
        }
    }

    override fun hasValidSession(): Boolean = userPreferences.hasValidSession()
}