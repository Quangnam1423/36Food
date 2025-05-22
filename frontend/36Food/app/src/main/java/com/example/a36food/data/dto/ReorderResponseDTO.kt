package com.example.a36food.data.dto

import com.google.gson.annotations.SerializedName

data class ReorderResponseDTO(
    @SerializedName("message")
    val message: String,

    @SerializedName("order")
    val order: OrderResponseDTO
)
