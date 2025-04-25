package com.example.a36food.data.network.base

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(
        val code: Int,
        val message: String,
        val errorBody: String? = null
    ) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getErrorOrNull(): Error? = when (this) {
        is Error -> this
        else -> null
    }
}