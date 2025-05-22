package com.example.a36food.data.dto

import com.google.gson.annotations.SerializedName

data class ItemReviewsResponseDTO(
    @SerializedName("reviews")
    val reviews: List<ReviewResponseDTO>,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("averageRating")
    val averageRating: Float
)
