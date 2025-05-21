package com.example.a36food.data.dto

data class OrderItemDTO(
    val id: Long,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?,
    val note: String
)

data class ErrorResponse(
    val error: String
)

data class OrderDetailResponse(
    val orderId: Long,
    val status: String,
    val orderDate: String,
    val updatedAt: String,
    val totalAmount: Double,
    val itemsTotal: Double,
    val deliveryFee: Double,
    val note: String,
    val restaurantId: String,
    val restaurantAddress: String,
    val customerAddress: String,
    val shippingAddress: String,
    val items: List<OrderItemDTO>
)
