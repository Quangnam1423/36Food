package com.example.a36food.domain.model


enum class OpeningStatus {
    OPEN_24H,
    TEMPORARY_CLOSED,
    SCHEDULED
}

data class BusinessHours(
    val openTime: String,
    val closeTime: String
)

data class Restaurant(
    val id: String,
    val name: String,
    val imageUrl: String,
    val rating: Float,
    val ratingCount: Int = 0,
    val address: String,
    val priceRange: String,
    val openingStatus: OpeningStatus = OpeningStatus.SCHEDULED, // kiểu lịch mở cửa
    val businessHours: BusinessHours? = null, // thời gian mở cửa
    val serviceType: ServiceType,
    val phoneNumber: String = "",
    val likes: Int = 0,
    val distance: Double = 0.0,
    val categories: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
