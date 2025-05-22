package com.example.a36food.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.network.NoConnectionException
import com.example.a36food.data.repository.LocationRepository
import com.example.a36food.data.repository.RestaurantRepository
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.domain.model.ServiceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteState(
    // Location states
    val isLocationLoading: Boolean = false,
    val userLatitude: Double = 0.0,
    val userLongitude: Double = 0.0,

    // Restaurant loading states
    val isInitialLoading: Boolean = false,  // First load or filter change
    val isLoadingMore: Boolean = false,     // Pagination loading
    val isRefreshing: Boolean = false,      // Pull-to-refresh

    // Restaurant data
    val favoriteRestaurants: List<Restaurant> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasMore: Boolean = false,
    val selectedServiceType: ServiceType = ServiceType.ALL,

    // Popular restaurants
    val popularRestaurants: List<Restaurant> = emptyList(),

    // Error handling
    val errorMessage: String? = null
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val locationRepository: LocationRepository,
    private val networkErrorHandler: NetworkErrorHandler
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteState())
    val state: StateFlow<FavoriteState> = _state.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    fun setNetworkErrorHandler(callback: () -> Unit) {
        _onNetworkError = callback
    }

    init {
        getUserLocation()
    }

    private fun getUserLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLocationLoading = true) }
            try {
                val location = locationRepository.getCurrentLocation()
                if (location != null) {
                    _state.update {
                        it.copy(
                            isLocationLoading = false,
                            userLatitude = location.latitude,
                            userLongitude = location.longitude
                        )
                    }
                    loadFavoriteRestaurants(forceRefresh = true)
                    loadPopularRestaurants()
                } else {
                    // Use default location if user location is not available
                    _state.update {
                        it.copy(
                            isLocationLoading = false,
                            userLatitude = 10.7758439,
                            userLongitude = 106.7017555
                        )
                    }
                    loadFavoriteRestaurants(forceRefresh = true)
                    loadPopularRestaurants()
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLocationLoading = false,
                        errorMessage = "Không thể lấy vị trí. Sử dụng vị trí mặc định.",
                        userLatitude = 10.7758439,
                        userLongitude = 106.7017555
                    )
                }
                loadFavoriteRestaurants(forceRefresh = true)
                loadPopularRestaurants()
            }
        }
    }

    fun loadFavoriteRestaurants(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                if (forceRefresh) {
                    _state.update { it.copy(isInitialLoading = true, currentPage = 0) }
                } else {
                    _state.update { it.copy(isLoadingMore = true) }
                }

                val currentState = _state.value
                val page = if (forceRefresh) 0 else currentState.currentPage
                val latitude = currentState.userLatitude
                val longitude = currentState.userLongitude

                val result = restaurantRepository.getFavoriteRestaurants(
                    latitude = latitude,
                    longitude = longitude,
                    page = page,
                    pageSize = 10
                )

                _state.update { currentState ->
                    val updatedRestaurants = if (forceRefresh) {
                        result.data
                    } else {
                        currentState.favoriteRestaurants + result.data
                    }

                    currentState.copy(
                        favoriteRestaurants = updatedRestaurants,
                        currentPage = result.currentPage + 1,
                        totalPages = result.totalPages,
                        hasMore = result.hasMore,
                        isInitialLoading = false,
                        isLoadingMore = false,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Đã xảy ra lỗi khi tải danh sách nhà hàng yêu thích")
            }
        }
    }

    private fun loadPopularRestaurants() {
        viewModelScope.launch {
            try {
                val currentState = _state.value
                val latitude = currentState.userLatitude
                val longitude = currentState.userLongitude

                val result = restaurantRepository.getPopularRestaurants(
                    latitude = latitude,
                    longitude = longitude,
                    page = 0,
                    size = 10
                )

                _state.update { currentState ->
                    currentState.copy(
                        popularRestaurants = result.data,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                // Just log the error but don't show to user
                // since this is secondary content
                println("Error loading popular restaurants: ${e.message}")
            }
        }
    }

    fun refreshData() {
        _state.update { it.copy(isRefreshing = true) }
        loadFavoriteRestaurants(forceRefresh = true)
        loadPopularRestaurants()
    }

    fun loadMoreFavorites() {
        val currentState = _state.value
        if (!currentState.isLoadingMore && currentState.hasMore) {
            loadFavoriteRestaurants(forceRefresh = false)
        }
    }

    fun setServiceType(serviceType: ServiceType) {
        if (_state.value.selectedServiceType != serviceType) {
            _state.update { it.copy(selectedServiceType = serviceType) }
            loadFavoriteRestaurants(forceRefresh = true)
        }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun handleError(errorMessage: String) {
        _state.update {
            it.copy(
                isInitialLoading = false,
                isLoadingMore = false,
                isRefreshing = false,
                errorMessage = errorMessage
            )
        }

        // Check for authentication errors
        if (errorMessage.contains("401") ||
            errorMessage.contains("Unauthorized") ||
            errorMessage.contains("token") ||
            errorMessage.contains("Không tìm thấy thông tin người dùng")) {
            // Notify the UI that user needs to re-authenticate
            _onNetworkError?.invoke()
        }
        // Check for network errors
        else if (errorMessage.contains("No connection") ||
            errorMessage.contains("Unable to resolve host") ||
            errorMessage.contains("timeout")) {
            _onNetworkError?.invoke()
        }
    }
}
