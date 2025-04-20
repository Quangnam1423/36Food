package com.example.a36food.domain.model

data class OrderItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String
)