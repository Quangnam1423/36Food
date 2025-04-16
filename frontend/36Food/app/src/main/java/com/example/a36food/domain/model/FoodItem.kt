package com.example.a36food.domain.model

data class FoodItem(
    val id: Int,
    val name: String,
    val price: Double,
    val restaurant: Restaurant? = null,
    val imageResId: Int,
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false,
    val like: Int = 0,
    val saleCount: Int = 0
)
