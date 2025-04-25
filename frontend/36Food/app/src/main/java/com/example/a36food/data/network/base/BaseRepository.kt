package com.example.a36food.data.network.base

import retrofit2.Response

abstract class BaseRepository {
    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): ApiResponse<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Unknown error occurred"
                )
            }
        } catch (e: Exception) {
            ApiResponse.Error(
                code = -1,
                message = e.message ?: "Unknown error occurred"
            )
        }
    }
}