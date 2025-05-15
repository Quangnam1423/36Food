package com.example.a36food.data.repository

import android.util.Log
import com.example.a36food.data.api.UserApi
import com.example.a36food.data.dto.ChangePasswordRequest
import com.example.a36food.data.network.NoConnectionException
import com.example.a36food.domain.model.User
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun getCurrentUser(token: String): User {
        try {
            val response = userApi.getCurrentUser("Bearer $token")
            if (response.isSuccessful) {
                return response.body() ?: throw Exception("Empty response body")
            } else {
                when (response.code()) {
                    401 -> throw Exception("Unauthorized: Token invalid or expired")
                    404 -> throw NoConnectionException()
                    else -> throw Exception("Failed to get user profile: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository",
                "Error fetching user profile",
                e
            )
            throw e
        }
    }

    suspend fun getUserAddress(latitude: Double, longitude: Double): String {
        try {
            val response = userApi.getUserAddress( latitude, longitude)
            if (response.isSuccessful) {
                return response.body() ?: throw Exception("Empty response body")
            } else {
                when (response.code()) {
                    404 -> throw NoConnectionException()
                    else -> throw Exception("Failed to get address: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error fetching user address", e)
            when (e) {
                is HttpException -> {
                    if (e.code() == 404) throw NoConnectionException()
                    throw Exception("Failed to fetch address: ${e.message()}")
                }
                is java.net.SocketTimeoutException,
                is java.io.IOException -> throw NoConnectionException()
                else -> throw e
            }
        }
    }

    suspend fun changePassword(token: String, request: ChangePasswordRequest): String {
        val response = userApi.changePassword(token, request)
        return response.string()
    }
}