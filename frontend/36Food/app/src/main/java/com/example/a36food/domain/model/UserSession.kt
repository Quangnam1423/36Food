package com.example.a36food.domain.model

data class UserSession(
    val token: String,
    val email: String,
    val userId: String
)
