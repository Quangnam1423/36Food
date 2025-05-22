package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.dto.CartItemRequest
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.CartRepository
import com.example.a36food.data.repository.LocationRepository
import com.example.a36food.data.repository.RestaurantRepository
import com.example.a36food.domain.model.CartItem
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
    val errorMessage: String? = null,
    val showAddToCartDiaLog: Boolean = false,
    val selectedFoodItem: FoodItem? = null,
    val itemQuantity: Int = 1,
    val itemNote: String = ""
)

data class CartOperationState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null,
    val cart: Unit? = null,
    val lastAddedItemId: String? = null
)

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val cartRepository: CartRepository,
    private val sharedPreferences: SharedPreferences,
    private val locationRepository: LocationRepository,
    private val networkErrorHandler: NetworkErrorHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(RestaurantDetailState())
    val state: StateFlow<RestaurantDetailState> = _state.asStateFlow()

    private val _cartState = MutableStateFlow(CartOperationState())
    val cartState: StateFlow<CartOperationState> = _cartState.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    fun setNetworkErrorHandler(handler: () -> Unit) {
        _onNetworkError = handler
    }

    init {
        // Extract restaurant ID from navigation arguments
        savedStateHandle.get<String>("restaurantId")?.let { id ->
            loadRestaurantDetails(id.toLong())
        }
    }

    fun showAddToCartDialog(food: FoodItem) {
        _state.update {
            it.copy(
                showAddToCartDiaLog = true,
                selectedFoodItem = food,
                itemQuantity = 1,
                itemNote = ""
            )
        }
    }

    fun hideAddToCartDialog() {
        _state.update{
            it.copy(showAddToCartDiaLog = false,
                selectedFoodItem = null
            )
        }
    }

    fun updateItemQuantity(quantity: Int) {
        if(quantity >= 1) {
            _state.update{it.copy(itemQuantity = quantity)}
        }
    }

    fun updateItemNote(note: String) {
        _state.update { it.copy(itemNote = note) }
    }

    fun addToCartWithDetails() {
        state.value.selectedFoodItem?.let { food ->
            viewModelScope.launch {
                _cartState.update { it.copy(isLoading = true, error = null) }

                val token = sharedPreferences.getString("access_token", null)
                if (token.isNullOrEmpty()) {
                    _cartState.update {
                        it.copy(
                            isLoading = false,
                            error = "Vui lòng đăng nhập để thêm món ăn vào giỏ hàng"
                        )
                    }
                    return@launch
                }

                val restaurantId = state.value.restaurant?.id

                val cartItem = restaurantId?.let {
                    CartItemRequest(
                        id = food.id,
                        name = food.name,
                        price = food.price,
                        quantity = state.value.itemQuantity,
                        imageUrl = food.imageUrl,
                        note = if (state.value.itemNote.isBlank()) null else state.value.itemNote,
                        restaurantId = it
                    )
                }

                val result = networkErrorHandler.safeApiCall(
                    apiCall = {
                        if (cartItem != null) {
                            cartRepository.addItemToCart(token, cartItem)
                        }
                    },
                    onNetworkError = { _onNetworkError?.invoke() }
                )

                result.fold(
                    onSuccess = { cart ->
                        _cartState.update {
                            it.copy(
                                isLoading = false,
                                success = "Đã thêm ${food.name} vào giỏ hàng",
                                cart = cart,
                                lastAddedItemId = food.id
                            )
                        }
                        hideAddToCartDialog()
                    },
                    onFailure = { error ->
                        _cartState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Không thể thêm món ăn vào giỏ hàng"
                            )
                        }
                    }
                )
            }
        }
    }

    fun addToCart(food: FoodItem) {
        viewModelScope.launch {
            _cartState.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _cartState.update {
                    it.copy(
                        isLoading = false,
                        error = "Vui lòng đăng nhập để thêm món ăn vào giỏ hàng"
                    )
                }
                return@launch
            }

            val restaurantId = state.value.restaurant?.id

            val cartItem = restaurantId?.let {
                CartItemRequest(
                    id = food.id,
                    name = food.name,
                    price = food.price,
                    quantity = state.value.itemQuantity,
                    imageUrl = food.imageUrl,
                    note = if (state.value.itemNote.isBlank()) null else state.value.itemNote,
                    restaurantId = it
                )
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = {
                    if (cartItem != null) {
                        cartRepository.addItemToCart(token, cartItem)
                    }
                },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { cart ->
                    _cartState.update {
                        it.copy(
                            isLoading = false,
                            success = "Đã thêm ${food.name} vào giỏ hàng",
                            cart = cart,
                            lastAddedItemId = food.id
                        )
                    }
                },
                onFailure = { error ->
                    _cartState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Không thể thêm món ăn vào giỏ hàng"
                        )
                    }
                }
            )
        }
    }

    fun clearCartMessage() {
        _cartState.update { it.copy(success = null, error = null, lastAddedItemId = null) }
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

    fun clearErrorMessage() {
        _state.update { currentState ->
            currentState.copy(errorMessage = null)
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

    /**
     * Toggle the favorite status of the restaurant
     */
    fun toggleFavorite() {
        val restaurantId = state.value.restaurant?.id ?: return

        viewModelScope.launch {
            try {
                val result = restaurantRepository.toggleFavoriteRestaurant(restaurantId)

                result.fold(
                    onSuccess = { (isFavorite, message) ->
                        // Update the restaurant in state with the new favorite status
                        _state.update { currentState ->
                            val updatedRestaurant = currentState.restaurant?.copy(isFavorite = isFavorite)
                            currentState.copy(restaurant = updatedRestaurant)
                        }

                        // Show success message
                        _cartState.update {
                            it.copy(success = message)
                        }
                    },
                    onFailure = { error ->
                        _cartState.update {
                            it.copy(error = error.message ?: "Không thể cập nhật trạng thái yêu thích")
                        }
                    }
                )
            } catch (e: Exception) {
                _cartState.update {
                    it.copy(error = e.message ?: "Đã xảy ra lỗi khi cập nhật trạng thái yêu thích")
                }
            }
        }
    }
}

