package com.example.a36food.data.model

import java.util.UUID

enum class UserRole {
    Customer,
    Owner,
    Delivery
}

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
