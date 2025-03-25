package com.example.a36food.data.remote

import com.example.a36food.domain.model.User
import retrofit2.http.Body
import retrofit2.http.POST

data class AuthRequest(
    val name: String? = null,
    val email: String,
    val password: String
)

data class AuthResponse(
    val id: String,
    val name: String,
    val email: String,
    val token: String?
) {
    fun toUser() : User {
        return User(
            id = id,
            name = name,
            email = email
        )
    }
}

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest) : AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse
}