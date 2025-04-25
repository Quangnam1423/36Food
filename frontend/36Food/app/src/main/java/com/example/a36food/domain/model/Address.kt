package com.example.a36food.domain.model

import java.util.UUID

data class Address(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val phoneNumber: String,
    val address: String,
    val isDefault: Boolean = false
)
