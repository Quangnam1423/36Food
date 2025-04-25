package com.example.a36food.domain.model

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val profileImage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)