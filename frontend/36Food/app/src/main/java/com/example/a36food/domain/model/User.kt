package com.example.a36food.domain.model

import java.util.UUID

data class User(
    val userId: String,
    val userSlug: String,
    val userName: String,
    val userEmail: String,
    val userPhone: String? = null,
    val userGender: String? = null,
    val userAvatar: String? = null,
    val userDob: String? = null,
    val userStatus: String? = null,
    val roleName: String? = null
)