package com.example.a36food.domain.model.requests

data class ChangePasswordRequest(
    val verifyToken: String,
    val newPassword: String
)
