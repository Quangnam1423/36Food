package com.example.a36food.data.api

import com.example.a36food.data.dto.CreateReviewRequestDTO
import com.example.a36food.data.dto.CreateReviewResponseDTO
import com.example.a36food.data.dto.ReviewResponseDTO
import com.example.a36food.data.dto.UserReviewsResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApi {
    @GET("reviews/item/{itemId}")
    suspend fun getItemReviews(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String
    ): Response<List<ReviewResponseDTO>>

    @GET("reviews/me")
    suspend fun getUserReviews(
        @Header("Authorization") token: String
    ): Response<UserReviewsResponseDTO>

    @POST("reviews")
    suspend fun createReview(
        @Header("Authorization") token: String,
        @Body reviewRequest: CreateReviewRequestDTO
    ): Response<CreateReviewResponseDTO>
}
