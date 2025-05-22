package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.dto.OrderResponseDTO
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.LocationRepository
import com.example.a36food.data.repository.OrderRepository
import com.example.a36food.data.repository.ReviewRepository
import com.example.a36food.domain.model.Order
import com.example.a36food.domain.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryState(
    val isLoading: Boolean = false,
    val orders: List<OrderResponseDTO> = emptyList(),
    val processingOrders: List<OrderResponseDTO> = emptyList(),
    val error: String? = null,
    val selectedStatus: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val orderDetails: Map<String, Any>? = null,
    val isLoadingDetails: Boolean = false,
    val historyOrders: List<OrderResponseDTO> = emptyList(),
    // Thêm các trường mới cho đánh giá
    val isLoadingReviews: Boolean = false,
    val userReviews: List<Review> = emptyList(),
    val totalReviewCount: Int = 0,
    val averageRating: Float = 0f,
    val isReviewMode: Boolean = false // Đánh dấu khi người dùng chọn tab đánh giá
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val reviewRepository: ReviewRepository,
    private val sharedPreferences: SharedPreferences,
    private val networkErrorHandler: NetworkErrorHandler,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null
    private var _onNavigateToOrderDetail: ((Long) -> Unit)? = null

    init {
        // Mặc định thiết lập khoảng thời gian 6 tháng kể từ hiện tại
        setDefaultDateRange()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDefaultDateRange() {
        val endDate = java.time.LocalDateTime.now()
        val startDate = endDate.minusMonths(6)

        val formatter = java.time.format.DateTimeFormatter.ISO_DATE_TIME

        _state.update {
            it.copy(
                startDate = startDate.format(formatter),
                endDate = endDate.format(formatter)
            )
        }
    }

    fun setNetworkErrorHandler(handler: () -> Unit) {
        _onNetworkError = handler
    }

    fun setNavigationHandler(handler: (Long) -> Unit) {
        _onNavigateToOrderDetail = handler
    }

    fun loadOrders(status: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Vui lòng đăng nhập để xem lịch sử đơn hàng") }
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = { orderRepository.getUserOrders(token, status) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { orders ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            orders = orders,
                            selectedStatus = status
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Không thể tải đơn hàng",
                            selectedStatus = status
                        )
                    }
                }
            )
        }
    }

    fun loadProcessingOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Vui lòng đăng nhập để xem đơn hàng đang xử lý") }
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = { orderRepository.getUserProcessingOrders(token) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { orders ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            processingOrders = orders
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Không thể tải đơn hàng đang xử lý"
                        )
                    }
                }
            )
        }
    }

    fun getProcessingOrders(): List<OrderResponseDTO> {
        return _state.value.processingOrders
    }

    fun getOrdersByStatus(status: String? = null): List<OrderResponseDTO> {
        val orders = _state.value.orders
        return when (status) {
            "PENDING", "CONFIRMED", "PREPARING", "READY", "DELIVERING" ->
                orders.filter { it.status in listOf("PENDING", "CONFIRMED", "PREPARING", "READY", "DELIVERING") }
            "COMPLETED" -> orders.filter { it.status == "COMPLETED" }
            "CANCELLED", "CANCELED" -> orders.filter { it.status in listOf("CANCELLED", "CANCELED") } // Support both spellings
            "DRAFT" -> orders.filter { it.status == "DRAFT" }
            null -> orders
            else -> emptyList()
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun getOrderDetails(orderId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingDetails = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoadingDetails = false, error = "Vui lòng đăng nhập để xem chi tiết đơn hàng") }
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = { orderRepository.getOrderDetails(token, orderId) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { details ->
                    _state.update { it.copy(isLoadingDetails = false, orderDetails = details) }
                    _onNavigateToOrderDetail?.invoke(orderId)
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoadingDetails = false,
                            error = exception.message ?: "Không thể tải chi tiết đơn hàng"
                        )
                    }
                }
            )
        }
    }

    fun onOrderClick(orderId: Long) {
        getOrderDetails(orderId)
    }

    fun clearOrderDetails() {
        _state.update { it.copy(orderDetails = null) }
    }

    fun loadHistoryOrders(status: String? = null, startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Vui lòng đăng nhập để xem lịch sử đơn hàng") }
                return@launch
            }

            // Sử dụng giá trị trong state nếu không có giá trị mới được cung cấp
            val effectiveStatus = status ?: _state.value.selectedStatus
            val effectiveStartDate = startDate ?: _state.value.startDate
            val effectiveEndDate = endDate ?: _state.value.endDate

            val result = networkErrorHandler.safeApiCall(
                apiCall = {
                    orderRepository.getUserOrdersWithFilter(
                        token,
                        effectiveStatus,
                        effectiveStartDate,
                        effectiveEndDate
                    )
                },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { orders ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            historyOrders = orders,
                            selectedStatus = effectiveStatus,
                            startDate = effectiveStartDate,
                            endDate = effectiveEndDate
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Không thể tải lịch sử đơn hàng"
                        )
                    }
                }
            )
        }
    }

    fun updateDateRange(startDate: String, endDate: String) {
        _state.update { it.copy(startDate = startDate, endDate = endDate) }
        loadHistoryOrders()
    }

    fun updateStatusFilter(status: String?) {
        _state.update { it.copy(selectedStatus = status) }
        loadHistoryOrders()
    }

    fun getHistoryOrders(): List<OrderResponseDTO> {
        return _state.value.historyOrders
    }

    // New function to cancel an order
    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Vui lòng đăng nhập để hủy đơn hàng") }
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = { orderRepository.cancelOrder(token, orderId) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { canceledOrder ->
                    // Update the list of processing orders to reflect the cancellation
                    loadProcessingOrders()

                    // Also reload orders with the current filters
                    loadOrders(_state.value.selectedStatus)

                    _state.update { it.copy(isLoading = false) }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Không thể hủy đơn hàng"
                        )
                    }
                }
            )
        }
    }

    // Function to load all user reviews
    fun loadUserReviews() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingReviews = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update {
                    it.copy(
                        isLoadingReviews = false,
                        error = "Vui lòng đăng nhập để xem đánh giá của bạn"
                    )
                }
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = { reviewRepository.getUserReviews(token) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { userReviewsResult ->
                    _state.update {
                        it.copy(
                            isLoadingReviews = false,
                            userReviews = userReviewsResult.reviews,
                            totalReviewCount = userReviewsResult.totalCount,
                            averageRating = userReviewsResult.averageRating
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoadingReviews = false,
                            error = exception.message ?: "Không thể tải đánh giá của bạn"
                        )
                    }
                }
            )
        }
    }

    // Toggle between order view and reviews view
    fun toggleReviewMode(isReviewMode: Boolean) {
        _state.update { it.copy(isReviewMode = isReviewMode) }

        // Load reviews data if entering review mode and reviews not loaded yet
        if (isReviewMode && _state.value.userReviews.isEmpty()) {
            loadUserReviews()
        }
    }

    // Get user reviews
    fun getUserReviews(): List<Review> {
        return _state.value.userReviews
    }

    fun reorderOrder(orderId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Vui lòng đăng nhập để đặt lại đơn hàng") }
                return@launch
            }
            val location = locationRepository.getCurrentLocation()

            val result = networkErrorHandler.safeApiCall(
                apiCall = { orderRepository.reorderOrder(token, orderId, location.latitude, location.longitude) },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { response ->
                    _state.update { it.copy(isLoading = false, error = null) }
                    // Refresh the orders list after successful reorder
                    loadOrders()
                    // Show success message
                    showSuccessMessage(response.message)
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun showSuccessMessage(message: String) {
        _state.update { it.copy(error = "Thành công: $message") }
    }

    fun submitReview(
        restaurantId: String,
        content: String,
        rating: Float,
        isAnonymous: Boolean,
        imageUrls: List<String>,
        foodId: String,
        orderId: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Vui lòng đăng nhập để gửi đánh giá") }
                return@launch
            }

            val result = networkErrorHandler.safeApiCall(
                apiCall = {
                    reviewRepository.createReview(
                        token = token,
                        restaurantId = restaurantId,
                        content = content,
                        rating = rating,
                        isAnonymous = isAnonymous,
                        imageUrls = imageUrls,
                        foodId = foodId,
                        orderId = orderId
                    )
                },
                onNetworkError = { _onNetworkError?.invoke() }
            )

            result.fold(
                onSuccess = { response ->
                    _state.update { it.copy(isLoading = false, error = null) }
                    // Reload user reviews after successful submission
                    loadUserReviews()
                    // Show success message
                    showSuccessMessage(response.message)
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }
}
