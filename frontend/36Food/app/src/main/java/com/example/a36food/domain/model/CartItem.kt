package com.example.a36food.domain.model

data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val note: String? = null
) {
    val totalPrice: Double
        get() = price * quantity
}