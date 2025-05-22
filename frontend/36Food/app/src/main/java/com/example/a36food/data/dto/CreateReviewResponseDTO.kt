package com.example.a36food.data.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CreateReviewResponseDTO(
    @SerializedName("message")
    val message: String,

    @SerializedName("reviewId")
    val reviewId: String,

    @SerializedName("review")
    val review: ReviewDetailDTO
)

data class ReviewDetailDTO(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("restaurantId")
    val restaurantId: String,

    @SerializedName("restaurantName")
    val restaurantName: String,

    @SerializedName("foodId")
    val foodId: String,

    @SerializedName("foodName")
    val foodName: String,

    @SerializedName("orderId")
    val orderId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("rating")
    val rating: Float,

    @SerializedName("imageUrls")
    val imageUrls: List<String> = emptyList(),

    @SerializedName("createdAt")
    val createdAt: Date,

    @SerializedName("isAnonymous")
    val isAnonymous: Boolean
)
