package com.example.a36food.domain.model.requests

data class VerifyCodeRequest(
    val email: String,
    val code: String
)
