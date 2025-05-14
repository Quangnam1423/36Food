package com.example.a36food.data.dto

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    val message: String,
    @SerializedName("accessToken") // For Gson
    val token: String? = null
)
