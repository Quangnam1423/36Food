package com.example.a36food.data.api

import com.example.a36food.domain.model.Restaurant
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantApi {
    @GET("restaurants")
    suspend fun getRestaurants(
        @Query("userLat") userLat: Double,
        @Query("userLng") userLng: Double
    ): List<Restaurant>

    @GET("restaurants/{id}")
    suspend fun getRestaurantDetail(
        @Path("id") id : Long,
        @Query("userLat") userLat: Double,
        @Query("userLng") userLng: Double
    ) : Restaurant
}