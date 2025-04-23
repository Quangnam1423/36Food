package com.example.a36food.domain.model

data class FoodReview(
    val review: Review,
    val foodId: String,
    val restaurantId: String,
    val orderId: String? = null // Link tới đơn hàng nếu là verified purchase
)
