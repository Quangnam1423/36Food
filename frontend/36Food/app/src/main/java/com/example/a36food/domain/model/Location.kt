package com.example.a36food.domain.model


enum class LocationLabel {
    HOME,
    OFFICE,
    OTHER
}

data class Location(
    val id: String,
    val userId: String,
    val address: String,
    val locationData: LocationData,
    val label: LocationLabel = LocationLabel.OTHER,
    val isCurrentLocation: Boolean = false,
    val isDefault: Boolean = false,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)