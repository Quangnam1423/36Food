package com.example.a36food.data.repository


import android.content.SharedPreferences
import com.example.a36food.data.api.OrderApi
import com.example.a36food.data.dto.OrderRequestDTO
import com.example.a36food.data.dto.OrderResponseDTO
import com.example.a36food.domain.model.Order
import com.example.a36food.domain.model.OrderItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderApi: OrderApi
) {
    suspend fun createOrder(token: String, orderRequest: OrderRequestDTO): OrderResponseDTO {
        val response = orderApi.createOrder("Bearer $token", orderRequest)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to create order: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun getOrderDetails(token: String, orderId: Long): Map<String, Any> {
        val response = orderApi.getOrderDetails("Bearer $token", orderId)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to get order details: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun cancelOrder(token: String, orderId: Long): OrderResponseDTO {
        val response = orderApi.cancelOrder("Bearer $token", orderId)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to cancel order: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun createDraftOrder(token: String): OrderResponseDTO {
        val response = orderApi.createDraftOrder("Bearer $token")

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to create draft order: ${response.code()} - ${response.message()}")
        }
    }
}