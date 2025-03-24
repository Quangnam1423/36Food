package com.example.a36food.data.model

enum class OrderStatus {
    Pending,
    Preparing,
    Delivering,
    Completed,
    Cancelled
}

data class OrderItem(
    val menuItemId: String,
    val quantity: Int,
    val price: Double
)

data class Order(
    val id: String,
    val userId: String,
    val restaurantId: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val status: OrderStatus,
    val createdAt: Long,
    val deliveryPersonId: String? = null
)
