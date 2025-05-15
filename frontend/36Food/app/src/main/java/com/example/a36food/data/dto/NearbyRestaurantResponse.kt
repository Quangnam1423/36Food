package com.example.a36food.data.dto

import com.example.a36food.domain.model.Restaurant

// RestaurantResponse.kt
data class NearbyRestaurantsResponse(
    val restaurants: List<Restaurant>,
    val currentPage: Int,
    val totalItems: Int,
    val totalPages: Int,
    val hasMore: Boolean
)