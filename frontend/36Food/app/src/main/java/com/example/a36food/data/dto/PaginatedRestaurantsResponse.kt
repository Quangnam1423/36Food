package com.example.a36food.data.dto

data class PaginatedRestaurantsResponse(
    val totalItems: Int,
    val totalPages: Int,
    val hasMore: Boolean,
    val restaurants: List<RestaurantDTO>,
    val currentPage: Int
)