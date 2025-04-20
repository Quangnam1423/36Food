package com.example.a36food.domain.model

data class OrderReview(
    val orderId: String,
    val restaurantName: String,
    val rating: Float,
    val comment: String,
    val reviewDate: Long,
    val reviewImages: List<String>,
    val isReviewed: Boolean
)