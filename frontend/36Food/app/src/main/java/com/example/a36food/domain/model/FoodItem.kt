package com.example.a36food.domain.model

data class FoodItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String = "",
    val price: Double,
    val imageUrl: String,
    val category: String, // Bắt buộc phải có category
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false,
    val likes: Int = 0,
    val saleCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
