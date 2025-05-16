package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.LocationRepository
import com.example.a36food.data.repository.PaginatedResult
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
    // Location states
    val isLocationLoading: Boolean = false,
    val userAddress: String = "Đang lấy vị trí ...",

    // Restaurant loading states
    val isInitialLoading: Boolean = false,  // First load or filter change
    val isLoadingMore: Boolean = false,     // Pagination loading
    val isRefreshing: Boolean = false,      // Pull-to-refresh

    // Restaurant data
    val restaurants: List<Restaurant> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasMore: Boolean = false,
    val selectedFilter: FilterOption = FilterOption.NEAR_ME,

    // Error handling
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
        loadInitialRestaurants()
    }

    private fun getUserLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLocationLoading = true) }
            try {
                val location = locationRepository.getCurrentLocation()
                if (location != null) {
                    getUserAddress(location.latitude, location.longitude)
                } else {
                    _state.update {
                        it.copy(
                            userAddress = "Không thể lấy vị trí",
                            isLocationLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        userAddress = "Không thể lấy vị trí",
                        isLocationLoading = false,
                        errorMessage = e.message
                    )
                }
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
                _state.update {
                    it.copy(
                        userAddress = address,
                        isLocationLoading = false
                    )
                }
            },
            onFailure = { error ->
                _state.update {
                    it.copy(
                        userAddress = "Không thể lấy địa chỉ",
                        errorMessage = error.message,
                        isLocationLoading = false
                    )
                }
            }
        )
    }

    // First load or when filter changes - starts from page 0
    private fun loadInitialRestaurants() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isInitialLoading = true,
                    isLoadingMore = false,
                    isRefreshing = false
                )
            }

            fetchRestaurants(isRefresh = true, page = 0)
        }
    }

    // Load more restaurants - pagination
    fun loadMoreRestaurants() {
        if (!state.value.isInitialLoading &&
            !state.value.isRefreshing &&
            !state.value.isLoadingMore &&
            state.value.hasMore) {

            val nextPage = state.value.currentPage + 1

            viewModelScope.launch {
                _state.update { it.copy(isLoadingMore = true) }
                fetchRestaurants(isRefresh = false, page = nextPage)
            }
        }
    }

    // Pull-to-refresh - refreshes without changing selected filter
    fun refreshData() {
        getUserLocation()

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRefreshing = true,
                    isInitialLoading = false,
                    isLoadingMore = false
                )
            }

            fetchRestaurants(isRefresh = true, page = 0)
        }
    }

    // Changes filter and triggers a reload
    fun setSelectedFilter(filter: FilterOption) {
        if (filter == state.value.selectedFilter) return

        _state.update {
            it.copy(
                selectedFilter = filter,
                restaurants = emptyList(),  // Clear existing restaurants
                currentPage = 0,
                totalPages = 0,
                hasMore = false
            )
        }

        loadInitialRestaurants()
    }

    // Core function to fetch restaurants based on selected filter
    private suspend fun fetchRestaurants(isRefresh: Boolean, page: Int) {
        try {
            val currentLocation = locationRepository.getCurrentLocation()
            if (currentLocation == null) {
                resetLoadingStates()
                return
            }

            val result = when (state.value.selectedFilter) {
                FilterOption.NEAR_ME -> restaurantRepository.getNearbyRestaurants(
                    latitude = currentLocation.latitude,
                    longitude = currentLocation.longitude,
                    page = page,
                    size = 10
                )
                FilterOption.POPULAR -> restaurantRepository.getPopularRestaurants(
                    latitude = currentLocation.latitude,
                    longitude = currentLocation.longitude,
                    page = page,
                    size = 10
                )
                FilterOption.TOP_RATED -> restaurantRepository.getTopRatedRestaurants(
                    latitude = currentLocation.latitude,
                    longitude = currentLocation.longitude,
                    page = page,
                    size = 10
                )
            }

            updateStateWithResults(result, isRefresh)

        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error fetching restaurants", e)
            handleError(e)
        }
    }


    private fun updateStateWithResults(
        result: PaginatedResult<Restaurant>,
        isRefresh: Boolean
    ) {
        _state.update { currentState ->
            val updatedList = if (isRefresh) {
                result.data
            } else {
                currentState.restaurants + result.data
            }

            currentState.copy(
                restaurants = updatedList,
                currentPage = result.currentPage,
                totalPages = result.totalPages,
                hasMore = result.hasMore,
                isInitialLoading = false,
                isRefreshing = false,
                isLoadingMore = false,
                errorMessage = null
            )
        }
    }

    private fun handleError(e: Exception) {
        _state.update {
            it.copy(
                errorMessage = "Không thể lấy danh sách nhà hàng: ${e.message}"
            )
        }
        resetLoadingStates()
        _onNetworkError?.invoke()
    }

    private fun resetLoadingStates() {
        _state.update {
            it.copy(
                isInitialLoading = false,
                isRefreshing = false,
                isLoadingMore = false
            )
        }
    }
}