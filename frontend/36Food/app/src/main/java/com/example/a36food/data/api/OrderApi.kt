package com.example.a36food.data.api

import com.example.a36food.data.dto.OrderDetailResponse
import com.example.a36food.data.dto.OrderRequestDTO
import com.example.a36food.data.dto.OrderResponseDTO
import com.example.a36food.domain.model.Order
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {
    @POST("orders/create")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body orderRequest: OrderRequestDTO
    ): Response<OrderResponseDTO>

    @GET("orders/user")
    suspend fun getUserOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): Response<List<OrderResponseDTO>>

    @GET("orders/{id}/details")
    suspend fun getOrderDetails(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long
    ): Response<Map<String, Any>>

    @PUT("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long
    ): Response<OrderResponseDTO>

    @POST("orders/draft")
    suspend fun createDraftOrder(
        @Header("Authorization") token: String
    ): Response<OrderResponseDTO>
}