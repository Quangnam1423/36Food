package com.example.a36food.data.dto

import com.example.a36food.domain.model.User
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("accessToken")
    val token: String?,

    @SerializedName("user")
    val userProfile: User? = null
)