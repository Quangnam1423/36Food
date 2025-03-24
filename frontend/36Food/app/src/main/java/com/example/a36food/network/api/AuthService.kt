package com.example.a36food.network.api

import com.example.a36food.data.model.User
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String,val password: String)
data class LoginResponse(val user: User, val token: String)

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}