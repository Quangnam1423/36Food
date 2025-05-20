package com.example.a36food.data.api

import com.example.a36food.data.dto.CartDTO
import com.example.a36food.data.dto.CartItemDTO
import com.example.a36food.data.dto.CartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CartApi {

    @GET("api/cart")
    suspend fun getUserCart(
        @Header("Authorization") token: String
    ): Response<CartDTO>

    @POST("api/cart/add")
    suspend fun addItemToCart(
        @Header("Authorization") token: String,
        @Body item: CartItemRequest?
    ): Response<CartDTO>

    @DELETE("api/cart/remove/{itemId}")
    suspend fun removeCartItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String
    ): Response<CartDTO>

    @PUT("api/cart/update/{itemId}")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String,
        @Query("quantity") quantity: Int
    ): Response<CartDTO>
}