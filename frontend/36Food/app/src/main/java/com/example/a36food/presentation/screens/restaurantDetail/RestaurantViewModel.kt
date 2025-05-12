package com.example.a36food.presentation.screens.restaurantDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.repository.RestaurantRepository
import com.example.a36food.domain.model.LocationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RestaurantViewModel @Inject constructor (
  private val repository: RestaurantRepository,
  savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow<RestaurantState>(RestaurantState.Loading)
    val state = _state.asStateFlow()

    private val restaurantId: Long = checkNotNull(savedStateHandle["restaurantId"])
    private val defaultLocation = LocationData(20.988528, 105.799062)

    init {
        loadRestaurantDetail()
    }

    private fun loadRestaurantDetail() {
        viewModelScope.launch {
            _state.value = RestaurantState.Loading

            try {
                val restaurant = repository.getRestaurantDetail(
                    id = restaurantId,
                    userLat = defaultLocation.latitude,
                    userLng = defaultLocation.longitude
                )
                _state.value = RestaurantState.Success(restaurant = restaurant)
            } catch (e: Exception) {
                _state.value = RestaurantState.Error(
                    e.message ?: "Không thể tải thông tin nhà hàng"
                )
            }
        }
    }

    fun retry() {
        loadRestaurantDetail()
    }
}