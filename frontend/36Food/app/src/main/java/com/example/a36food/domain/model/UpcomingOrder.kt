package com.example.a36food.domain.model

data class UpcomingOrder(
    val order: Order,
    val estimatedDeliveryTime: Long,
    val deliveryPersonName: String,
    val deliveryPersonPhone: String,
    val currentLocation: String,
    val trackingStatus: String
)