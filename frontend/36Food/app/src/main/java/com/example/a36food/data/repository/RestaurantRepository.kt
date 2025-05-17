package com.example.a36food.data.repository

import android.util.Log
import com.example.a36food.data.api.RestaurantApi
import com.example.a36food.data.dto.MenuItemDTO
import com.example.a36food.data.dto.toDomainModels
import com.example.a36food.data.network.NoConnectionException
import com.example.a36food.domain.model.Restaurant
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


data class PaginatedResult<T>(
    val data: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val hasMore: Boolean
)

@Singleton
class RestaurantRepository @Inject constructor(
    private val restaurantApi: RestaurantApi
) {
    suspend fun getNearbyRestaurants(
        latitude: Double,
        longitude: Double,
        page: Int = 0,
        size: Int = 10,
        radiusInKm: Int = 10
    ): PaginatedResult<Restaurant> {
        try {
            Log.d("RestaurantRepository", "Fetching nearby restaurants: lat=$latitude, lng=$longitude, page=$page, size=$size")
            val response = restaurantApi.getNearbyRestaurantsPaged(
                userLat = latitude,
                userLng = longitude,
                page = page,
                pageSize = size,
                radiusInKm = radiusInKm
            )

            val restaurants = response.restaurants.toDomainModels()

            return PaginatedResult(
                data = restaurants,
                currentPage = response.currentPage,
                totalPages = response.totalPages,
                hasMore = response.hasMore
            )
        } catch (e: Exception) {
            Log.e("RestaurantRepository", "Error fetching nearby restaurants", e)
            when (e) {
                is HttpException -> {
                    throw Exception("Failed to fetch nearby restaurants: ${e.message()}")
                }
                is java.net.SocketTimeoutException,
                is IOException -> throw NoConnectionException()
                else -> throw e
            }
        }
    }

    suspend fun getPopularRestaurants(
        latitude: Double,
        longitude: Double,
        page: Int = 0,
        size: Int = 10
    ): PaginatedResult<Restaurant> {
        try {
            Log.d("RestaurantRepository", "Fetching nearby restaurants: lat=$latitude, lng=$longitude, page=$page, size=$size")
            val response = restaurantApi.getPopularRestaurants(
                userLat = latitude,
                userLng = longitude,
                page = page,
                pageSize = size
            )

            val restaurants = response.restaurants.toDomainModels()

            return PaginatedResult(
                data = restaurants,
                currentPage = response.currentPage,
                totalPages = response.totalPages,
                hasMore = response.hasMore
            )
        } catch (e: Exception) {
            Log.e("RestaurantRepository", "Error fetching nearby restaurants", e)
            when (e) {
                is HttpException -> {
                    throw Exception("Failed to fetch popular restaurants: ${e.message()}")
                }
                is java.net.SocketTimeoutException,
                is IOException -> throw NoConnectionException()
                else -> throw e
            }
        }
    }

    suspend fun getTopRatedRestaurants(
        latitude: Double,
        longitude: Double,
        page: Int = 0,
        size: Int = 10
    ): PaginatedResult<Restaurant> {
        try {
            Log.d(
                "RestaurantRepository",
                "Fetching nearby restaurants: lat=$latitude, lng=$longitude, page=$page, size=$size"
            )
            val response = restaurantApi.getTopRatedRestaurants(
                userLat = latitude,
                userLng = longitude,
                page = page,
                pageSize = size
            )

            val restaurants = response.restaurants.toDomainModels()

            return PaginatedResult(
                data = restaurants,
                currentPage = response.currentPage,
                totalPages = response.totalPages,
                hasMore = response.hasMore
            )
        } catch (e: Exception) {
            Log.e("RestaurantRepository", "Error fetching nearby restaurants", e)
            when (e) {
                is HttpException -> {
                    throw Exception("Failed to fetch Top Rate restaurants: ${e.message()}")
                }

                is java.net.SocketTimeoutException,
                is IOException -> throw NoConnectionException()

                else -> throw e
            }
        }
    }

    suspend fun getRestaurantDetail(id: String, latitude: Double, longitude: Double): Restaurant {
        try {
            return restaurantApi.getRestaurantDetail(id.toLong(), latitude, longitude).toDomainModel()
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

    suspend fun getMenuItems(
        restaurantId: Long,
        categoryName: String? = null
    ): List<MenuItemDTO> {
        return restaurantApi.getMenuItems(restaurantId, categoryName)
    }
}