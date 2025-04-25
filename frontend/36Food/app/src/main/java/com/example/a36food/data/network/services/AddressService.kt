package com.example.a36food.data.network.services

import com.example.a36food.domain.model.Address
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AddressService {
    @GET("user/addresses")
    suspend fun getUserAddresses(): Response<List<Address>>

    @POST("user/addresses")
    suspend fun addAddress(@Body address: Address): Response<Address>

    @DELETE("user/addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: String): Response<Unit>
}