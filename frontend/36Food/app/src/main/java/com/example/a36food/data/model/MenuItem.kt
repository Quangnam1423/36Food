package com.example.a36food.data.model

import java.util.UUID

data class MenuItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val image: String? = null,
    val description: String? = null,
    val restaurantId: String
)
