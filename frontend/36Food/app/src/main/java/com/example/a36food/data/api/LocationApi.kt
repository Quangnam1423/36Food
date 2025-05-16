package com.example.a36food.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApi {
    @GET("user/get-address")
    suspend fun getUserAddress(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ) : Map<String, String>
}