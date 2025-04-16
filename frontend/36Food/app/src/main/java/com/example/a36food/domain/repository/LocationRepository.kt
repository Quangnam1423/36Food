package com.example.a36food.domain.repository

import com.example.a36food.domain.model.LocationData

interface LocationRepository {
    suspend fun getCurrentLocation() : LocationData?
}