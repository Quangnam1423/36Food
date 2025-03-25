package com.example.a36food.data.repository

import com.example.a36food.data.remote.AuthApiService
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

    override suspend fun resgisterUser(name: String, email: String, password: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(email: String, password: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun logoutUser() {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUser(): User? {
        TODO("Not yet implemented")
    }


}