package com.example.a36food.domain.repository

import com.example.a36food.domain.model.User

interface AuthRepository {
    suspend fun resgisterUser(name: String, email: String, password: String): User
    suspend fun loginUser(email: String, password: String): User
    suspend fun logoutUser()
    suspend fun getCurrentUser(): User?
}