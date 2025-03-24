package com.example.a36food.data.model

import java.util.UUID

data class Restaurant (
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val ownerId: String,
    val address: String,
    val phoneNumber: String,
    val rating: Float = 0.0f,
    val createdAt: Long = System.currentTimeMillis(),
    val menu: List<MenuItem> = emptyList()
)