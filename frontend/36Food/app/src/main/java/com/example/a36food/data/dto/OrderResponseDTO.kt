package com.example.a36food.data.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class OrderResponseDTO(
    val orderId: Long,
    val status: String?, // Đã xóa SerializedName và để status có thể null
    val orderDate: Date,
    val updatedAt: Date?,
    val totalAmount: Double,
    val itemsTotal: Double,
    val deliveryFee: Double,
    val note: String?,
    val restaurantId: String?,
    val restaurantAddress: String?,
    val customerAddress: String?,
    val shippingAddress: String?,
    val items: List<OrderItemDTO> = emptyList()
)

