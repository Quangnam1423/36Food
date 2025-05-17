package com.example.a36food.data.dto

import com.example.a36food.domain.model.FoodItem

data class MenuItemDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val category: String,
    val isAvailable: Boolean = true
) {
    // Convert to domain model for use in the app
    fun toDomainModel(): FoodItem {
        return FoodItem(
            id = id.toString(),
            restaurantId = "", // You'll need to set this from outside if needed
            name = name,
            description = description ?: "",
            price = price,
            imageUrl = imageUrl ?: "",
            category = category,
            isAvailable = isAvailable
        )
    }
}