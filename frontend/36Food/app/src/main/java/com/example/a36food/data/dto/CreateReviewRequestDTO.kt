package com.example.a36food.data.dto

import com.google.gson.annotations.SerializedName

data class CreateReviewRequestDTO(
    @SerializedName("restaurantId")
    val restaurantId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("rating")
    val rating: Float,

    @SerializedName("isAnonymous")
    val isAnonymous: Boolean,

    @SerializedName("imageUrls")
    val imageUrls: List<String> = emptyList(),

    @SerializedName("foodId")
    val foodId: String,

    @SerializedName("orderId")
    val orderId: String
)
