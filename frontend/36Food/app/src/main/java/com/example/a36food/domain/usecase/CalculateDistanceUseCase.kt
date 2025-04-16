package com.example.a36food.domain.usecase

import com.example.a36food.domain.model.LocationData
import kotlin.math.*

class CalculateDistanceUseCase {
    operator fun invoke(
        userLocation: LocationData,
        placeLocation: LocationData
    ) : Double {
        val earthRadius = 6371.0 // km

        val dLat = Math.toRadians(placeLocation.latitude - userLocation.latitude)
        val dLon = Math.toRadians(placeLocation.longitude - userLocation.longitude)

        val lat1 = Math.toRadians(userLocation.latitude)
        val lat2 = Math.toRadians(placeLocation.latitude)

        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}