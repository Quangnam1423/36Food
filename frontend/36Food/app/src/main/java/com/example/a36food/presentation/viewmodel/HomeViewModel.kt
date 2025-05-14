package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.AuthRepository
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.domain.model.User
import com.example.a36food.presentation.screens.homes.FilterOption
import com.example.a36food.ui.viewmodel.LocationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


data class HomeState(
    val isLoading: Boolean = false,
    val isLoadingRestaurants: Boolean = false,
    val user: User? = null,
    val userAddress: String? = null,
    val restaurants: List<Restaurant> = emptyList(),
    val filteredRestaurants: List<Restaurant> = emptyList(),
    val selectedFilter: FilterOption = FilterOption.NEAR_ME,
    val error: String? = null
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository,
    private val networkErrorHandler: NetworkErrorHandler,
    private val locationViewModel: LocationViewModel,
    private val sharedPreferences: SharedPreferences
) {
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    private var _onNetworkError: (() -> Unit)? = null

    init {
        //getCurrentUser()

        viewModelScope.launch {
            locationViewModel.locationData.collect { location ->
                location?.let {
                    fetchUserAddress(it.latitude, it.longitude)
                    fetchNearbyRestaurants(it.latitude, it.longitude)
                }
            }
        }
    }
    }
}