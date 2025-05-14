package com.example.a36food.data.dto

data class RegisterRequest(
    val userEmail: String,
    val userName: String,
    val userPassword: String,
    val userPhone: String,
    val userGender: String?,
    val userDob: String?,
    val userSlug: String,
    val roleId: Int,
    val userStatus: String = "ACTIVE",
    val userAvatar: String? = null,
    val userSalt: String? = null
)