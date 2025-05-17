package com.example.a36food.data.api

import com.example.a36food.data.dto.MenuItemDTO
import com.example.a36food.data.dto.PaginatedRestaurantsResponse
import com.example.a36food.data.dto.RestaurantDTO
import com.example.a36food.domain.model.Restaurant
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantApi {
    @GET("restaurants")
    suspend fun getNearbyRestaurants(
        @Query("userLat") userLat: Double,
        @Query("userLng") userLng: Double
    ): List<Restaurant>

    @GET("restaurants/{id}")
    suspend fun getRestaurantDetail(
        @Path("id") id : Long,
        @Query("userLat") userLat: Double,
        @Query("userLng") userLng: Double
    ) : RestaurantDTO

    @GET("restaurants/nearby-paged")
    suspend fun getNearbyRestaurantsPaged(
        @Query("userLat") userLat: Double,
        @Query("userLng") userLng: Double,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("radiusInKm") radiusInKm: Int = 10,
    ): PaginatedRestaurantsResponse

    @GET("restaurants/popular")
    suspend fun getPopularRestaurants(
        @Query("userLat") userLat: Double,
        @Query("userLng") userLng: Double,
        @Query("page") page: Int,
        @Query("size") pageSize: Int
    ): PaginatedRestaurantsResponse

    @GET("restaurants/top-rated")
    suspend fun getTopRatedRestaurants(
        @Query("userLat") userLat: Double,
        @Query("userLng") userLng: Double,
        @Query("page") page: Int,
        @Query("size") pageSize: Int
    ): PaginatedRestaurantsResponse

    @GET("restaurants/{id}/menu-items")
    suspend fun getMenuItems(
        @Path("id") id: Long,
        @Query("categoryName") categoryName: String? = null
    ) : List<MenuItemDTO>
}