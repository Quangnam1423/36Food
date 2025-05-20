package com.example.a36food.data.dto

data class CartDTO(
    val id: String,
    val restaurantId: String,
    val items: List<CartItemDTO> = emptyList()
)

data class CartItemDTO(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val note: String? = null
)