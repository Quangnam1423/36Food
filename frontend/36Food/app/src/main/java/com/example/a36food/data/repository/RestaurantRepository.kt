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


    suspend fun searchRestaurants(
        keyword: String,
        latitude: Double,
        longitude: Double,
        searchBy: String = "all"
    ): PaginatedResult<Restaurant> {
        try {
            Log.d(
                "RestaurantRepository",
                "Searching restaurants: keyword=$keyword, searchBy=$searchBy"
            )
            val response = restaurantApi.searchRestaurants(
                keyword = keyword,
                userLat = latitude,
                userLng = longitude,
                searchBy = searchBy
            )

            val restaurants = response.restaurants.toDomainModels()

            return PaginatedResult(
                data = restaurants,
                currentPage = 0, // API doesn't support pagination for search yet
                totalPages = 1,   // Assuming all results are returned at once
                hasMore = false   // No pagination for search results
            )
        } catch (e: Exception) {
            Log.e("RestaurantRepository", "Error searching restaurants", e)
            when (e) {
                is HttpException -> {
                    throw Exception("Failed to search restaurants: ${e.message()}")
                }

                is java.net.SocketTimeoutException,
                is IOException -> throw NoConnectionException()

                else -> throw e
            }
        }
    }

    suspend fun getFavoriteRestaurants(
        latitude: Double,
        longitude: Double,
        page: Int = 0,
        pageSize: Int = 10
    ): PaginatedResult<Restaurant> {
        try {
            Log.d("RestaurantRepository", "Fetching favorite restaurants: lat=$latitude, lng=$longitude")
            val response = restaurantApi.getFavoriteRestaurants(
                latitude = latitude,
                longitude = longitude
            )

            val restaurants = response.restaurants.toDomainModels()

            // Since the new API doesn't provide pagination details, we need to simulate them
            // We'll treat the entire response as a single page
            return PaginatedResult(
                data = restaurants,
                currentPage = 0,
                totalPages = 1,
                hasMore = false
            )
        } catch (e: Exception) {
            Log.e("RestaurantRepository", "Error fetching favorite restaurants", e)
            when (e) {
                is HttpException -> {
                    throw Exception("Failed to fetch favorite restaurants: ${e.message()}")
                }
                is java.net.SocketTimeoutException,
                is IOException -> throw NoConnectionException()
                else -> throw e
            }
        }
    }

    /**
     * Toggle a restaurant's favorite status
     * @param restaurantId The ID of the restaurant to toggle
     * @return A Pair containing the new favorite status (true if added, false if removed) and a message
     */
    suspend fun toggleFavoriteRestaurant(restaurantId: String): Result<Pair<Boolean, String>> {
        return try {
            Log.d("RestaurantRepository", "Toggling favorite status for restaurant: $restaurantId")
            val response = restaurantApi.toggleFavoriteRestaurant(restaurantId)

            val isFavorite = response["isFavorite"] as Boolean
            val message = response["message"] as String

            Result.success(Pair(isFavorite, message))
        } catch (e: Exception) {
            Log.e("RestaurantRepository", "Error toggling favorite status", e)
            when (e) {
                is HttpException -> {
                    Result.failure(Exception("Failed to toggle favorite status: ${e.message()}"))
                }
                is java.net.SocketTimeoutException,
                is IOException -> Result.failure(NoConnectionException())
                else -> Result.failure(e)
            }
        }
    }
}
