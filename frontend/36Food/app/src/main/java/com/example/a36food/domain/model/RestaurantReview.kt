package com.example.a36food.domain.model

data class RestaurantReview(
    val review: Review,
    val restaurantId: String,
    val orderId: String? = null // Link tới đơn hàng nếu là verified purchase
)
