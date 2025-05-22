package com.example.a36food.data.repository


import android.content.SharedPreferences
import com.example.a36food.data.api.OrderApi
import com.example.a36food.data.dto.OrderRequestDTO
import com.example.a36food.data.dto.OrderResponseDTO
import com.example.a36food.data.dto.ReorderRequestDTO
import com.example.a36food.data.dto.ReorderResponseDTO
import com.example.a36food.domain.model.Order
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
            // Đọc nội dung lỗi từ body của response
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to create order: ${response.code()} - ${if (!errorBody.isNullOrEmpty()) errorBody else response.message()}")
        }
    }

    suspend fun getUserOrders(token: String, status: String? = null): List<OrderResponseDTO> {
        val response = orderApi.getUserOrders("Bearer $token", status)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to get user orders: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun getUserOrdersWithFilter(
        token: String,
        status: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): List<OrderResponseDTO> {
        val response = orderApi.getUserOrdersWithFilter("Bearer $token", status, startDate, endDate)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to get filtered orders: ${response.code()} - ${response.message()}")
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

    suspend fun getUserProcessingOrders(token: String): List<OrderResponseDTO> {
        val response = orderApi.getUserProcessingOrders("Bearer $token")

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Failed to get processing orders: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun reorderOrder(token: String, orderId: Long, latitude: Double, longitude: Double): ReorderResponseDTO {
        val reorderRequest = ReorderRequestDTO(orderId, latitude, longitude)
        val response = orderApi.reorderOrder("Bearer $token", reorderRequest)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to reorder: ${response.code()} - ${if (!errorBody.isNullOrEmpty()) errorBody else response.message()}")
        }
    }
}
