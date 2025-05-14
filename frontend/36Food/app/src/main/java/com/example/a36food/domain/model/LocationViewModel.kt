package com.example.a36food.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.repository.LocationRepository
import com.example.a36food.domain.model.LocationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    val locationData: StateFlow<LocationData?> = locationRepository.locationData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    private val _isLocationEnabled = MutableStateFlow(locationRepository.isLocationEnabled())
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled

    init {
        checkLocationSettings()
        requestLocationUpdates()
    }

    fun checkLocationSettings() {
        _isLocationEnabled.value = locationRepository.isLocationEnabled()
    }

    fun requestLocationUpdates() {
        locationRepository.requestLocationUpdates()
    }

    fun hasLocationPermission(): Boolean {
        return locationRepository.hasLocationPermission()
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            locationRepository.getCurrentLocation()
        }
    }
}