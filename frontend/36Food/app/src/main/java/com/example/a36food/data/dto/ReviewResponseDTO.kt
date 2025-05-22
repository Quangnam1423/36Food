package com.example.a36food.data.dto

import com.example.a36food.domain.model.Review
import com.google.gson.annotations.SerializedName
import java.util.Date

data class ReviewResponseDTO(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("rating")
    val rating: Float,

    @SerializedName("imageUrls")
    val imageUrls: List<String> = emptyList(),

    @SerializedName("createdAt")
    val createdAt: Date,

    @SerializedName("isAnonymous")
    val isAnonymous: Boolean,

    @SerializedName("foodId")
    val foodId: String,

    @SerializedName("restaurantId")
    val restaurantId: String,

    @SerializedName("orderId")
    val orderId: String? = null,

    @SerializedName("userName")
    val userName: String? = null,

    @SerializedName("userAvatar")
    val userAvatar: String? = null
)

// Extension function to convert DTO to domain model
fun ReviewResponseDTO.toReview(): Review {
    return Review(
        id = id,
        userId = userId,
        content = content,
        rating = rating,
        imageUrls = imageUrls,
        createdAt = createdAt,
        isAnonymous = isAnonymous,
        foodId = foodId,
        restaurantId = restaurantId,
        orderId = orderId
    )
}

// Extension function to convert a list of DTOs to domain models
fun List<ReviewResponseDTO>.toReviewList(): List<Review> {
    return this.map { it.toReview() }
}
