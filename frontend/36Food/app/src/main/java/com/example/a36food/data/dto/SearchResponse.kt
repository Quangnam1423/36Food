package com.example.a36food.data.dto

data class SearchResponse(
    val restaurants: List<RestaurantDTO>,
    val totalCount: Int,
    val keyword: String,
    val searchBy: String
)