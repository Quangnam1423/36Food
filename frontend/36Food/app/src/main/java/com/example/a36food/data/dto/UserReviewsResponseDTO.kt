package com.example.a36food.data.dto

import com.example.a36food.domain.model.Review
import com.google.gson.annotations.SerializedName

data class UserReviewsResponseDTO(
    @SerializedName("reviews")
    val reviews: List<Review>,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("averageRating")
    val averageRating: Float
)
