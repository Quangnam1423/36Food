package com.example.a36food.data.dto

data class OrderResponseDTO(
    val orderId: Long,
    val orderStatus: String,
    val orderDate: String,   // ISO-8601 format string cho LocalDateTime
    val updatedAt: String?,  // Có thể null
    val totalAmount: Double,
    val deliveryFee: Double?,
    val restaurantAddress: String?,  // Địa chỉ nhà hàng (nơi giao)
    val customerAddress: String?     // Địa chỉ khách hàng (nơi nhận)
)
