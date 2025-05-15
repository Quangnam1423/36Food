package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.LocationRepository
import com.example.a36food.data.repository.RestaurantRepository
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.presentation.screens.homes.FilterOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState (
    val isLoading: Boolean = false,
    val userAddress: String = "Đang lấy vị trí ...",
    val restaurants: List<Restaurant> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasMore: Boolean = false,
    val selectedFilter: FilterOption = FilterOption.NEAR_ME,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val locationRepository: LocationRepository,
    private val networkErrorHandler: NetworkErrorHandler,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    fun setNetworkErrorHandler(callback: () -> Unit) {
        _onNetworkError = callback
    }

    init {
        getUserLocation()
    }

    private fun getUserLocation() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val location = locationRepository.getCurrentLocation()
                if (location != null) {
                    getUserAddress(location.latitude, location.longitude)
                    //getNearbyRestaurants(location.latitude, location.longitude)
                }
                else {
                    _state.value = _state.value.copy(
                        userAddress = "Không thể lấy vị trí",
                        isLoading = false
                    )
                }
            } catch (e : Exception) {
                _state.value = _state.value.copy(
                    userAddress = "Không thể lấy vị trí",
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    private suspend fun getUserAddress(latitude: Double, longitude: Double) {
        val result = networkErrorHandler.safeApiCall(
            apiCall = { locationRepository.getUserAddress(latitude, longitude) },
            onNetworkError = { _onNetworkError?.invoke() }
        )

        result.fold (
            onSuccess = {address ->
                _state.value = _state.value.copy(userAddress = address)
            },
            onFailure = {
                _state.value = _state.value.copy(
                    userAddress = "Không thể lấy địa chỉ",
                    errorMessage = it.message
                )
            }
        )
    }
    fun loadMore() {
        if (_state.value.isLoading || !_state.value.hasMore) return

        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation() ?: return@launch
            getNearbyRestaurants(location.latitude, location.longitude, _state.value.currentPage + 1)
        }
    }

    fun setFilterOption(option: FilterOption) {
        if (_state.value.selectedFilter == option) return

        _state.value = _state.value.copy(
            selectedFilter = option,
            restaurants = emptyList(),
            currentPage = 0
        )

        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation() ?: return@launch
            when (option) {
                FilterOption.NEAR_ME -> getNearbyRestaurants(location.latitude, location.longitude)
                FilterOption.POPULAR -> getPopularRestaurants(location.latitude, location.longitude)
                FilterOption.TOP_RATED -> getTopRatedRestaurants(location.latitude, location.longitude)
            }
        }
    }

    private suspend fun getPopularRestaurants(latitude: Double, longitude: Double, page: Int = 0) {
        // Similar to getNearbyRestaurants but with different sorting parameter
        // For now we'll use the same implementation
        getNearbyRestaurants(latitude, longitude, page)
    }

    private suspend fun getTopRatedRestaurants(latitude: Double, longitude: Double, page: Int = 0) {
        // Similar to getNearbyRestaurants but with different sorting parameter
        // For now we'll use the same implementation
        getNearbyRestaurants(latitude, longitude, page)
    }
}