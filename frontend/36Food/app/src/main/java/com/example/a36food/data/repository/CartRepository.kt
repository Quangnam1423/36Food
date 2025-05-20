package com.example.a36food.data.repository

import android.util.Log
import com.example.a36food.data.api.CartApi
import com.example.a36food.data.dto.CartItemDTO
import com.example.a36food.data.dto.CartItemRequest
import com.example.a36food.data.network.NoConnectionException
import com.example.a36food.domain.model.Cart
import com.example.a36food.domain.model.CartItem
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartApi: CartApi
) {
    suspend fun getUserCart(token: String): Result<Cart> {
        return try {
            Log.d("CartRepository", "Getting user cart")

            val response = cartApi.getUserCart(token = "Bearer $token")

            if (response.isSuccessful) {
                val cartDTO = response.body()
                if (cartDTO != null) {
                    val cart = Cart(
                        id = cartDTO.id,
                        restaurantId = cartDTO.restaurantId,
                        items = cartDTO.items.map { dto ->
                            CartItem(
                                id = dto.id,
                                name = dto.name,
                                price = dto.price,
                                quantity = dto.quantity,
                                imageUrl = dto.imageUrl,
                                note = dto.note
                            )
                        }
                    )
                    Result.success(cart)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Failed to get cart: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Error getting user cart", e)
            when (e) {
                is HttpException -> Result.failure(Exception("Network error: ${e.message()}"))
                is IOException -> Result.failure(NoConnectionException())
                else -> Result.failure(e)
            }
        }
    }

    suspend fun addItemToCart(token: String, item: CartItemRequest): Result<Cart> {
        return try {
            if (item != null) {
                Log.d("CartRepository", "Adding item to cart: ${item.name}")
            }


            val response = cartApi.addItemToCart(token = "Bearer $token", item)

            if (response.isSuccessful) {
                val cartDTO = response.body()
                if (cartDTO != null) {
                    val cart = Cart(
                        id = cartDTO.id,
                        restaurantId = cartDTO.restaurantId,
                        items = cartDTO.items.map { dto ->
                            CartItem(
                                id = dto.id,
                                name = dto.name,
                                price = dto.price,
                                quantity = dto.quantity,
                                imageUrl = dto.imageUrl,
                                note = dto.note
                            )
                        }
                    )
                    Result.success(cart)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Failed to add item to cart: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Error adding item to cart", e)
            when (e) {
                is HttpException -> Result.failure(Exception("Network error: ${e.message()}"))
                is IOException -> Result.failure(NoConnectionException())
                else -> Result.failure(e)
            }
        }
    }



    suspend fun removeCartItem(token: String, itemId: String): Result<Cart> {
        return try {
            val bearerToken = "Bearer $token"
            val response = cartApi.removeCartItem(bearerToken, itemId)

            if (response.isSuccessful) {
                val cartDTO = response.body()
                if (cartDTO != null) {

                    val cart = Cart(
                        id = cartDTO.id,
                        restaurantId = cartDTO.restaurantId,
                        items = cartDTO.items.map { dto ->
                            CartItem(
                                id = dto.id,
                                name = dto.name,
                                price = dto.price,
                                quantity = dto.quantity,
                                imageUrl = dto.imageUrl,
                                note = dto.note
                            )
                        }
                    )
                    Result.success(cart)
                } else {
                    Result.failure(Exception("Response body is empty"))
                }
            } else {
                Result.failure(Exception("Failed to remove item: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCartItem(token: String, itemId: String, cartItem: CartItem): Result<Cart> {
        return try {
            val bearerToken = "Bearer $token"
            val response = cartApi.updateCartItem(bearerToken, itemId, cartItem.quantity)

            if (response.isSuccessful) {
                val cartDTO = response.body()
                if (cartDTO != null) {

                    val cart = Cart(
                        id = cartDTO.id,
                        restaurantId = cartDTO.restaurantId,
                        items = cartDTO.items.map { dto ->
                            CartItem(
                                id = dto.id,
                                name = dto.name,
                                price = dto.price,
                                quantity = dto.quantity,
                                imageUrl = dto.imageUrl,
                                note = dto.note
                            )
                        }
                    )
                    Result.success(cart)
                } else {
                    Result.failure(Exception("Response body is empty"))
                }
            } else {
                Result.failure(Exception("Failed to update item: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}