package com.example.a36food.data.repository

import android.util.Log
import com.example.a36food.data.api.ReviewApi
import com.example.a36food.data.dto.UserReviewsResponseDTO
import com.example.a36food.data.dto.ItemReviewsResponseDTO
import com.example.a36food.data.dto.toReviewList
import com.example.a36food.domain.model.Review
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val reviewApi: ReviewApi
) {
    suspend fun getItemReviews(token: String, itemId: String): List<Review> {
        val response = reviewApi.getItemReviews("Bearer $token", itemId)

        if (response.isSuccessful) {
            return response.body()?.toReviewList() ?: emptyList()
        } else {
            throw Exception("Failed to get reviews: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun getUserReviews(token: String): UserReviewsResponseDTO {
        try {
            val response = reviewApi.getUserReviews("Bearer $token")

            if (response.isSuccessful) {
                val userReviewsDTO = response.body()
                if (userReviewsDTO != null) {
                    return userReviewsDTO
                } else {
                    throw Exception("Empty response body")
                }
            } else {
                throw Exception("Failed to get user reviews: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("ReviewRepository", "Error getting user reviews", e)
            throw e
        }
    }
}
