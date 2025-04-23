package com.example.a36food.domain.model

data class Category(
    val id: String,
    val restaurantId: String, // nhà hàng có thể tùy biến kiểu món ăn trên menu của mình
    val name: String,
    val imageUrl: String? = null,
    val description: String = "",
    val isActive: Boolean = true,
    val order: Int = 0
)