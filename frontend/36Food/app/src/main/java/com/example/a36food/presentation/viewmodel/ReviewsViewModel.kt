package com.example.a36food.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.ReviewRepository
import com.example.a36food.domain.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserReviewsState(
    val isLoading: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val totalCount: Int = 0,
    val averageRating: Float = 0f,
    val error: String? = null
)

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val sharedPreferences: SharedPreferences,
    private val networkErrorHandler: NetworkErrorHandler
) : ViewModel() {

    private val _state = MutableStateFlow(UserReviewsState())
    val state: StateFlow<UserReviewsState> = _state.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    fun setNetworkErrorHandler(handler: () -> Unit) {
        _onNetworkError = handler
    }

    fun loadUserReviews() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val token = sharedPreferences.getString("access_token", null)
            if (token.isNullOrEmpty()) {
                _state.update {
                    it.copy(
                        isLoading = false,
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
                            isLoading = false,
                            reviews = userReviewsResult.reviews,
                            totalCount = userReviewsResult.totalCount,
                            averageRating = userReviewsResult.averageRating
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Không thể tải đánh giá của bạn"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
