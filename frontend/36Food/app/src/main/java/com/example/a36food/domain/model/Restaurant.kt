package com.example.a36food.domain.model

import android.provider.ContactsContract.CommonDataKinds.Phone
import java.util.UUID

data class Restaurant(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val password: String,
    val numberPhone: Phone,
    val status: String,
    val verify: Boolean,
    val role: UserRole
)
