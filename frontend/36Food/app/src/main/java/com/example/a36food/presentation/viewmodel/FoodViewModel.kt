package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.dto.CartItemRequest
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.CartRepository
import com.example.a36food.data.repository.ReviewRepository
import com.example.a36food.domain.model.FoodItem
import com.example.a36food.domain.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodScreenState(
    val foodItem: FoodItem? = null,
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val isReviewsLoading: Boolean = false,
    val error: String? = null,
    val quantity: Int = 1,
    val note: String = "",
    val averageRating: Float = 0f,
    val showAddToCartDiaLog: Boolean = false
)

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val sharedPreferences: SharedPreferences,
    private val cartRepository: CartRepository,
    private val networkErrorHandler: NetworkErrorHandler
) : ViewModel() {

    private val _state = MutableStateFlow(FoodScreenState())
    val state: StateFlow<FoodScreenState> = _state.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    fun setNetworkErrorHandler(handler: () -> Unit) {
        _onNetworkError = handler
    }

    fun setFoodItem(foodItem: FoodItem) {
        _state.update { it.copy(foodItem = foodItem) }
        loadReviews(foodItem.id)
    }

    fun updateQuantity(quantity: Int) {
        if (quantity >= 1) {
            _state.update { it.copy(quantity = quantity) }
        }
    }

    fun updateNote(note: String) {
        _state.update { it.copy(note = note) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun addToCartWithDetails() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }

                val token = sharedPreferences.getString("access_token", null)
                if (token.isNullOrEmpty()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Vui lòng đăng nhập để thêm món ăn vào giỏ hàng"
                        )
                    }
                    return@launch
                }

                val restaurantId = _state.value.foodItem?.restaurantId

                val cartItem = restaurantId?.let {
                    _state.value.foodItem?.let { it1 ->
                        CartItemRequest(
                            id = _state.value.foodItem!!.id,
                            name = _state.value.foodItem!!.name,
                            price = _state.value.foodItem!!.price,
                            quantity = _state.value.quantity,
                            imageUrl = it1.imageUrl,
                            note = if (state.value.note.isBlank()) null else state.value.note,
                            restaurantId = it
                        )
                    }
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
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Đã thêm ${_state.value.foodItem!!.name} vào giỏ hàng",
                            )
                        }
                        hideAddToCartDialog()
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Không thể thêm món ăn vào giỏ hàng"
                            )
                        }
                    }
                )
            }
    }

    fun hideAddToCartDialog() {
        _state.update { it.copy(showAddToCartDiaLog = false) }
    }

    fun showAddToCartDialog() {
        _state.update {it.copy(showAddToCartDiaLog = true)}
    }

    private fun loadReviews(foodId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isReviewsLoading = true) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update {
                    it.copy(
                        isReviewsLoading = false,
                        error = "Vui lòng đăng nhập để xem đánh giá"
                    )
                }
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = { reviewRepository.getItemReviews(token, foodId) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { reviews ->
                    val averageRating = if (reviews.isNotEmpty()) {
                        reviews.map { it.rating }.average().toFloat()
                    } else {
                        0f
                    }

                    _state.update {
                        it.copy(
                            isReviewsLoading = false,
                            reviews = reviews,
                            averageRating = averageRating
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isReviewsLoading = false,
                            error = exception.message ?: "Không thể tải đánh giá"
                        )
                    }
                }
            )
        }
    }
}
