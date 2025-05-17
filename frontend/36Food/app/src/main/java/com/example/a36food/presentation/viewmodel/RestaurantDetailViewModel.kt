package com.example.a36food.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.dto.MenuItemDTO
import com.example.a36food.data.repository.LocationRepository
import com.example.a36food.data.repository.RestaurantRepository
import com.example.a36food.domain.model.FoodItem
import com.example.a36food.domain.model.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RestaurantDetailState(
    val restaurant: Restaurant? = null,
    val isLoading: Boolean = false,
    val isMenuLoading: Boolean = false,
    val menuItems: List<FoodItem> = emptyList(),
    val menuCategories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(RestaurantDetailState())
    val state: StateFlow<RestaurantDetailState> = _state.asStateFlow()

    init {
        // Extract restaurant ID from navigation arguments
        savedStateHandle.get<String>("restaurantId")?.let { id ->
            loadRestaurantDetails(id.toLong())
        }
    }

    private fun loadRestaurantDetails(restaurantId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val location = locationRepository.getCurrentLocation()
                if (location != null) {
                    val restaurant = restaurantRepository.getRestaurantDetail(
                        restaurantId.toString(),
                        location.latitude,
                        location.longitude
                    )

                    _state.update {
                        it.copy(
                            restaurant = restaurant,
                            isLoading = false
                        )
                    }

                    // Load all menu items initially
                    loadMenuItems(restaurantId)
                } else {
                    _state.update {
                        it.copy(
                            errorMessage = "Không thể lấy vị trí của bạn",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("RestaurantDetailViewModel", "Error loading restaurant details", e)
                _state.update {
                    it.copy(
                        errorMessage = "Không thể tải thông tin nhà hàng: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadMenuItems(restaurantId: Long, categoryName: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isMenuLoading = true, errorMessage = null) }

            try {
                val menuItemDTOs = restaurantRepository.getMenuItems(restaurantId, categoryName)

                // Convert DTOs to domain models and set restaurant ID
                val menuItems = menuItemDTOs.map {
                    it.toDomainModel().copy(restaurantId = restaurantId.toString())
                }

                // Extract unique categories from all menu items if no specific category was requested
                val allCategories = if (categoryName == null) {
                    // If we're loading all items, get all categories
                    val categories = menuItems.map { it.category }.distinct().sorted()
                    categories
                } else {
                    // Otherwise maintain existing categories
                    state.value.menuCategories
                }

                _state.update {
                    it.copy(
                        menuItems = menuItems,
                        menuCategories = allCategories,
                        selectedCategory = categoryName,
                        isMenuLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("RestaurantDetailViewModel", "Error loading menu items", e)
                _state.update {
                    it.copy(
                        errorMessage = "Không thể tải menu: ${e.message}",
                        isMenuLoading = false
                    )
                }
            }
        }
    }

    fun selectCategory(categoryName: String?) {
        if (categoryName == state.value.selectedCategory) return

        state.value.restaurant?.id?.let { restaurantId ->
            loadMenuItems(restaurantId.toLong(), categoryName)
        }
    }

    fun refreshData() {
        state.value.restaurant?.id?.let { restaurantId ->
            loadRestaurantDetails(restaurantId.toLong())
        }
    }
}