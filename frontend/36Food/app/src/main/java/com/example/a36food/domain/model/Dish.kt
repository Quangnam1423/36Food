package com.example.a36food.domain.model

import java.util.UUID

enum class FoodCategory{
    COM,
    PHO,
    PIZZA,
    BURGER,
    DO_AN_NHANH,
    DO_UONG
}

data class Dish(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val imageUrl: String,
    val price: Double,
    val description: String,
    val category: FoodCategory,
    val isAvailable: Boolean = true
)
