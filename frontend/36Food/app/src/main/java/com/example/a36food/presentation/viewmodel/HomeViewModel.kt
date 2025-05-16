package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import android.util.Log
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
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
        fetchNearbyRestaurants(refresh  = true)
    }

    private fun getUserLocation() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val location = locationRepository.getCurrentLocation()
                if (location != null) {
                    getUserAddress(location.latitude, location.longitude)
                } else {
                    _state.value = _state.value.copy(
                        userAddress = "Không thể lấy vị trí",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
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

        result.fold(
            onSuccess = { address ->
                _state.value = _state.value.copy(
                    userAddress = address,
                    isLoading = false
                )
            },
            onFailure = { error ->
                _state.value = _state.value.copy(
                    userAddress = "Không thể lấy địa chỉ",
                    errorMessage = error.message,
                    isLoading = false
                )
            }
        )
    }

    fun fetchNearbyRestaurants(refresh: Boolean = false) {
        val nextPage = if (refresh) 0 else state.value.currentPage + 1

        if (state.value.isLoadingMore || (!state.value.hasMore && !refresh && nextPage > 0)) {
            return
        }

        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        isLoading = refresh,
                        isLoadingMore = !refresh && nextPage > 0
                    )
                }
                var currentLocation = locationRepository.getCurrentLocation()
                // Check if we have location
                if (currentLocation == null) {
                    // Wait for location to be available
                    currentLocation = locationRepository.getCurrentLocation()
                    if (currentLocation == null) {
                        _state.update {
                            it.copy(
                                errorMessage = "Vui lòng bật vị trí để tìm nhà hàng gần bạn",
                                isLoading = false,
                                isLoadingMore = false
                            )
                        }
                        return@launch
                    }
                }

                val result = restaurantRepository.getNearbyRestaurants(
                    latitude = currentLocation!!.latitude,
                    longitude = currentLocation!!.longitude,
                    page = nextPage,
                    size = 10
                )

                _state.update { currentState ->
                    val updatedList = if (refresh) {
                        result.data
                    } else {
                        currentState.restaurants + result.data
                    }

                    currentState.copy(
                        restaurants = updatedList,
                        currentPage = result.currentPage,
                        totalPages = result.totalPages,
                        hasMore = result.hasMore,
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching restaurants", e)
                _state.update {
                    it.copy(
                        errorMessage = "Không thể lấy danh sách nhà hàng: ${e.message}",
                        isLoading = false,
                        isLoadingMore = false
                    )
                }
            }
        }
    }

    fun setSelectedFilter(filter: FilterOption) {
        _state.update { it.copy(selectedFilter = filter) }
        // Reload restaurants with the new filter
        fetchNearbyRestaurants(refresh = true)
    }

    fun loadMoreRestaurants() {
        if (!state.value.isLoading && !state.value.isLoadingMore && state.value.hasMore) {
            fetchNearbyRestaurants(refresh = false)
        }
    }

    fun refreshData() {
        fetchNearbyRestaurants(refresh = true)
    }
}