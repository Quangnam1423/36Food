package com.example.a36food.data.repository

import com.example.a36food.data.remote.AuthApiService
import com.example.a36food.data.remote.AuthRequest
import com.example.a36food.domain.model.User
import javax.inject.Inject

interface AuthRepository {
    suspend fun resgisterUser(name: String, email: String, password: String): User
    suspend fun loginUser(email: String, password: String): User
    suspend fun logoutUser()
    suspend fun getCurrentUser(): User?
}

class AuthRepositoryImpl @Inject constructor(
    private val authApi : AuthApiService
) : AuthRepository{

    private var cachedUser: User? = null
    private var token: String? = null

    override suspend fun resgisterUser(name: String, email: String, password: String): User {
        val request = AuthRequest(name = name, email = email, password = password)
        val response = authApi.register(request)

        val user = response.toUser()

        token = response.token

        cachedUser = user

        return user
    }

    override suspend fun loginUser(email: String, password: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun logoutUser() {
        cachedUser = null
        token = null
    }

    override suspend fun getCurrentUser(): User? {
        return cachedUser
    }
}