package com.example.a36food.domain.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds

object LocationManager {
    private var fusedLocationClient: FusedLocationProviderClient? = null

    fun initialize(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun getCurrentLocation(context: Context): Location {
        return suspendCancellableCoroutine { continuation ->
            if (checkLocationPermission(context)) {
                fusedLocationClient?.lastLocation
                    ?.addOnSuccessListener { location ->
                        location?.let {
                            continuation.resume(it, null)
                        } ?: continuation.resumeWithException(Exception("Location not available"))
                    }
                    ?.addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
            } else {
                continuation.resumeWithException(Exception("Location permission not granted"))
            }
        }
    }

    fun getLocationUpdates(context: Context): Flow<Location> = callbackFlow {
        if (!checkLocationPermission(context)) {
            throw Exception("Location permission not granted")
        }

        val locationRequest = LocationRequest.Builder(5.seconds.inWholeMilliseconds)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location)
                }
            }
        }

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermission(context: Context): Boolean = checkLocationPermission(context)
}