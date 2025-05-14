package com.example.a36food.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.a36food.domain.model.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _locationData = MutableStateFlow<LocationData?>(null)
    val locationData: StateFlow<LocationData?> = _locationData

    private val defaultLocation = LocationData(20.988528, 105.799062)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData {
        if (!hasLocationPermission() || !isLocationEnabled()) {
            return defaultLocation
        }

        return try {
            val cancellationToken = CancellationTokenSource()

            suspendCancellableCoroutine { continuation ->
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        val locationData = LocationData(location.latitude, location.longitude)
                        _locationData.value = locationData
                        continuation.resume(locationData)
                    } else {
                        _locationData.value = defaultLocation
                        continuation.resume(defaultLocation)
                    }
                }.addOnFailureListener {
                    _locationData.value = defaultLocation
                    continuation.resume(defaultLocation)
                }

                continuation.invokeOnCancellation {
                    cancellationToken.cancel()
                }
            }
        } catch (e: Exception) {
            defaultLocation
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        if (!hasLocationPermission() || !isLocationEnabled()) {
            _locationData.value = defaultLocation
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _locationData.value = LocationData(location.latitude, location.longitude)
                } else {
                    _locationData.value = defaultLocation
                }
            }
        } catch (e: Exception) {
            _locationData.value = defaultLocation
        }
    }
}