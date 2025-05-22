package com.example.a36food.domain.model

import java.util.Date

data class Review(
    val id: String,
    val userId: String,
    val content: String,
    val rating: Float,
    val imageUrls: List<String> = emptyList(),
    val createdAt: Date,
    val isAnonymous: Boolean,
    val foodId: String,
    val restaurantId: String,
    val orderId: String? = null
)