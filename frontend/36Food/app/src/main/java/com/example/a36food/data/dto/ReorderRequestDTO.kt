package com.example.a36food.data.dto

import com.google.gson.annotations.SerializedName

data class ReorderRequestDTO(
    @SerializedName("orderId")
    val orderId: Long,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)
