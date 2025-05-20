package com.example.a36food.domain.model

data class Cart(
    val id: String,
    val restaurantId: String?,
    val items: List<CartItem> = emptyList()
) {
    val totalPrice: Double
        get() = items.sumOf { it.totalPrice }

    val totalItems: Int
        get() = items.sumOf { it.quantity }
}
