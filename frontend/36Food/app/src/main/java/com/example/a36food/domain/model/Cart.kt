package com.example.a36food.domain.model

data class Cart(
    val restaurantId: String?,
    val items: List<CartItem> = emptyList(),
) {
    val totalPrice: Double
        get() = items.sumOf { it.price * it.quantity }

    val itemCount: Int
        get() = items.sumOf { it.quantity }
}
