package com.example.a36food.domain.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.CompletableDeferred
import java.util.Locale

data class LocationData(
    val latitude: Double,
    val longitude: Double
) {
    suspend fun toAddress(context: Context): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val result = CompletableDeferred<String>()
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    val address = addresses.firstOrNull()
                    result.complete(formatAddress(address))
                }
                result.await()
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val address = addresses?.firstOrNull()
                formatAddress(address)
            }
        } catch (e: Exception) {
            "$latitude, $longitude"
        }
    }

    private fun formatAddress(address: Address?): String {
        if (address == null) return "$latitude, $longitude"
        return buildString {
            address.getAddressLine(0)?.let { append(it) }
        }
    }
}