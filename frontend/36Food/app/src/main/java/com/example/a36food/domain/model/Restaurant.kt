package com.example.a36food.domain.model

data class Restaurant(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val rating: Float,
    val address: String,
    val priceRange: String,
    val isOpen: Boolean
)
