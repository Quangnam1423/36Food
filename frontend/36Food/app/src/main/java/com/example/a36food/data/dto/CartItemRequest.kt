package com.example.a36food.data.dto

data class CartItemRequest(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val note: String? = null,
    val restaurantId: String
)