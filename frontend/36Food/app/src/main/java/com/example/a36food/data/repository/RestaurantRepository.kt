package com.example.a36food.data.repository

import com.example.a36food.data.api.RestaurantApi
import com.example.a36food.data.network.NoConnectionException
import com.example.a36food.domain.model.Restaurant
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantRepository @Inject constructor(
    private val restaurantApi: RestaurantApi
) {
    suspend fun getNearbyRestaurants(latitude: Double, longitude: Double): List<Restaurant> {
        try {
            return restaurantApi.getNearbyRestaurants(latitude, longitude)
        } catch (e: Exception) {
            android.util.Log.e("RestaurantRepository", "Error fetching nearby restaurants", e)
            when (e) {
                is HttpException -> {
                    if (e.code() == 404) throw NoConnectionException()
                    throw Exception("Failed to fetch restaurants: ${e.message()}")
                }
                is java.net.SocketTimeoutException,
                is java.io.IOException -> throw NoConnectionException()
                else -> throw e
            }
        }
    }

    suspend fun getRestaurantDetail(id: String, latitude: Double, longitude: Double): Restaurant {
        try {
            return restaurantApi.getRestaurantDetail(id.toLong(), latitude, longitude)
        } catch (e: Exception) {
            android.util.Log.e("RestaurantRepository", "Error fetching restaurant details", e)
            when (e) {
                is HttpException -> {
                    if (e.code() == 404) throw Exception("Restaurant not found")
                    throw Exception("Failed to fetch restaurant details: ${e.message()}")
                }
                is java.net.SocketTimeoutException,
                is java.io.IOException -> throw NoConnectionException()
                else -> throw e
            }
        }
    }
}