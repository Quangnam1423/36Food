package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.CartRepository
import com.example.a36food.domain.model.Cart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class CartState(
    val isLoading: Boolean = false,
    val cart: Result<Cart>? = null,
    val error: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val sharedPreferences: SharedPreferences,
    private val networkErrorHandler: NetworkErrorHandler
) : ViewModel() {
    private val _state = MutableStateFlow(CartState())
    val state : StateFlow<CartState> = _state.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    fun setNetworkErrorHandler(handler: () -> Unit) {
        _onNetworkError = handler
    }

    fun loadCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Vui lòng đăng nhập để xem giỏ hàng") }
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = { cartRepository.getUserCart(token) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { cart ->
                    _state.update { it.copy(isLoading = false, cart = cart) }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Không thể tải giỏ hàng"
                        )
                    }
                }
            )
        }
    }

    fun removeCartItem(itemId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Vui lòng đăng nhập để thao tác giỏ hàng") }
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = { cartRepository.removeCartItem(token, itemId) },
                onNetworkError = { _onNetworkError?.invoke() }
            )
            result.fold(
                onSuccess = { cart ->
                    _state.update { it.copy(isLoading = false, cart = cart) }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Không thể xóa món ăn khỏi giỏ hàng"
                        )
                    }
                }
            )
        }
        loadCart()
    }

    fun updateCartItemQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Vui lòng đăng nhập để thao tác giỏ hàng") }
                return@launch
            }

            val currentCart = state.value.cart?.getOrNull()
            val cartItem = currentCart?.items?.find { it.id == itemId }

            if (cartItem == null) {
                _state.update { it.copy(isLoading = false, error = "Không tìm thấy món ăn trong giỏ hàng") }
                return@launch
            }

            val updatedItem = cartItem.copy(quantity = quantity)

            val result = networkErrorHandler.safeApiCall(
                apiCall = { cartRepository.updateCartItem(token, itemId, updatedItem) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { cart ->
                    _state.update { it.copy(isLoading = false, cart = cart) }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Không thể cập nhật số lượng"
                        )
                    }
                }
            )
        }
        loadCart()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}