package com.example.a36food.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId") val userId: String,
    @SerializedName("userSlug") val userSlug: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userEmail") val userEmail: String,
    @SerializedName("userPhone") val userPhone: String? = null,
    @SerializedName("userGender") val userGender: String? = null,
    @SerializedName("userAvatar") val userAvatar: String? = null,
    @SerializedName("userDob") val userDob: String? = null,
    @SerializedName("userStatus") val userStatus: String? = null,
    @SerializedName("roleName") val roleName: String? = null,
    @SerializedName("userAddress") val userAddress: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)