package com.example.a36food.data.dto

import com.example.a36food.domain.model.BusinessHours
import com.example.a36food.domain.model.OpeningStatus
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.domain.model.ServiceType
import com.google.gson.annotations.SerializedName

data class RestaurantDTO(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val rating: Float,
    val ratingCount: Int,
    val address: String,
    val priceRange: String,
    val openingStatus: String,
    val businessHours: String?,
    val serviceType: String,
    val phoneNumber: String,
    val likes: Int,
    val reviewsCount: Int,
    val distance: Double,
    val categories: List<String>,
    val createdAt: Long,
    val durationInMinutes: Int?,
    val orderCount: Long?,
    val isFavorite: Boolean = false
) {
    fun toDomainModel(): Restaurant {
        // Parse opening status
        val status = try {
            OpeningStatus.valueOf(openingStatus)
        } catch (e: Exception) {
            OpeningStatus.SCHEDULED
        }

        // Parse business hours based on opening status
        val hours = when (status) {
            OpeningStatus.SCHEDULED -> parseBusinessHours(businessHours)
            OpeningStatus.OPEN_24H, OpeningStatus.TEMPORARY_CLOSED -> null
        }

        // Parse service type
        val service = try {
            ServiceType.valueOf(serviceType)
        } catch (e: Exception) {
            ServiceType.ALL
        }

        return Restaurant(
            id = id.toString(),
            name = name,
            imageUrl = imageUrl,
            rating = rating,
            ratingCount = ratingCount,
            address = address,
            priceRange = priceRange,
            openingStatus = status,
            businessHours = hours,
            serviceType = service,
            phoneNumber = phoneNumber,
            likes = likes,
            reviewsCount = reviewsCount,
            distance = distance,
            categories = categories,
            createdAt = createdAt,
            durationInMinutes = durationInMinutes,
            orderCount = orderCount,
            isFavorite = isFavorite
        )
    }

    private fun parseBusinessHours(hoursString: String?): BusinessHours? {
        if (hoursString.isNullOrEmpty()) return null

        // Format expected: "09:00-22:00" or similar
        val parts = hoursString.split("-")
        return if (parts.size == 2) {
            BusinessHours(
                openTime = parts[0].trim(),
                closeTime = parts[1].trim()
            )
        } else null
    }
}

fun List<RestaurantDTO>.toDomainModels(): List<Restaurant> {
    return map { it.toDomainModel() }
}