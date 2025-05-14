package com.example.a36food.data.repository

import com.example.a36food.data.api.UserApi
import com.example.a36food.data.dto.LoginRequest
import com.example.a36food.data.dto.LoginResponse
import com.example.a36food.data.network.NoConnectionException
import com.example.a36food.domain.model.User
import com.example.a36food.data.dto.RegisterRequest
import com.example.a36food.data.dto.RegisterResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: UserApi
) {
    suspend fun loginWithToken(token: String): LoginResponse {
        try {
            // Format token as Bearer token for header
            val bearerToken = "Bearer $token"
            val response = authApi.loginWithToken(bearerToken)
            android.util.Log.d("AuthRepository", "Token login response code: ${response.code()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    android.util.Log.d("AuthRepository", "Token login successful, user: ${responseBody.userProfile?.userName}")
                    return responseBody
                } else {
                    throw Exception("Empty response body")
                }
            } else {
                when (response.code()) {
                    401 -> throw Exception("Token invalid or expired")
                    404 -> throw NoConnectionException()
                    else -> throw Exception("Token login failed with code: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Exception during token login", e)
            throw e
        }
    }

    suspend fun register(registerRequest: RegisterRequest): RegisterResponse {
        try {
            val response = authApi.register(registerRequest)
            android.util.Log.d("AuthRepository", "Register response code: ${response.code()}")

            if (response.isSuccessful) {
                return response.body() ?: RegisterResponse("Registration successful")
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AuthRepository", "Registration error: $errorBody")

                when (response.code()) {
                    400 -> throw Exception(errorBody ?: "Bad request: Check your input")
                    409 -> throw Exception("Email already in use")
                    404 -> throw NoConnectionException()
                    else -> throw Exception("Registration failed with code: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Exception during registration", e)
            throw e
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        try {
            val response = authApi.login(LoginRequest(email, password))
            android.util.Log.d("AuthRepository", "Login response code: ${response.code()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    android.util.Log.d("AuthRepository", "Response body: token=${responseBody.token?.take(10) ?: "null"}, userProfile=${responseBody.userProfile != null}")
                    return responseBody
                } else {
                    throw Exception("Empty response body")
                }
            } else {
                when (response.code()) {
                    401 -> throw Exception(response.errorBody()?.string() ?: "Unauthorized: Email or password incorrect")
                    404 -> throw NoConnectionException()
                    else -> throw Exception("Login failed with code: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Exception during login", e)
            throw e
        }
    }

    suspend fun verifyToken(token: String): Boolean {
        return try {
            val response = authApi.getCurrentUser("Bearer $token")
            android.util.Log.d("AuthRepository", "Token verification response: ${response.code()}, Body: ${response.body()}")
            response.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Token verification error: ${e.message}", e)
            if (e is java.net.SocketTimeoutException || e is java.io.IOException) {
                throw NoConnectionException()
            }
            // For other exceptions, token is probably invalid
            false
        }
    }

    suspend fun getCurrentUser(token: String): User {
        val response = authApi.getCurrentUser("Bearer $token")

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            when (response.code()) {
                401 -> throw Exception("Unauthorized: Token invalid or expired")
                404 -> throw Exception("User not found")
                else -> throw Exception("Failed to get user profile with code: ${response.code()}")
            }
        }
    }
}