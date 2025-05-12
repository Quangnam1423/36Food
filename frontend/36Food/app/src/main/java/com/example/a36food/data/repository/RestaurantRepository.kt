package com.example.a36food.data.repository

import com.example.a36food.data.api.RestaurantApi
import com.example.a36food.domain.model.Restaurant
import javax.inject.Inject

class RestaurantRepository @Inject constructor(
    private val api: RestaurantApi
) {
    suspend fun getRestaurants(userLat: Double, userLng: Double): Result<List<Restaurant>> =
        try {
            Result.success(api.getRestaurants(userLat, userLng))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getRestaurantDetail(
        id: Long,
        userLat: Double,
        userLng: Double
    ) : Result<Restaurant> =
        try {
            Result.success(api.getRestaurantDetail(id, userLat, userLng))
        } catch (e: Exception) {
            Result.failure(e)
        }
}