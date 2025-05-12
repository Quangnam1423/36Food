package com.example.a36food.presentation.screens.restaurantDetail

import com.example.a36food.domain.model.Restaurant

sealed interface RestaurantState {
    data class Success(val restaurant: Result<Restaurant>) : RestaurantState
    data object Loading : RestaurantState
    data class Error(val message: String) : RestaurantState
}