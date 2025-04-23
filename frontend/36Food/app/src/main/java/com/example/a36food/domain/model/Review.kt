package com.example.a36food.domain.model

data class Review(
    val id: String,
    val userId: String,
    val content: String,
    val rating: Float, // 1-5 stars
    val imageUrls: List<String> = emptyList(),
    val likes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
