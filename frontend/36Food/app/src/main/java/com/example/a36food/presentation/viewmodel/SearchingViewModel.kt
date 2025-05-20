package com.example.a36food.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a36food.data.local.RecentSearchManager
import com.example.a36food.data.network.NetworkErrorHandler
import com.example.a36food.data.repository.LocationRepository
import com.example.a36food.data.repository.RestaurantRepository
import com.example.a36food.domain.model.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SearchUiState(
    val isLoading: Boolean = false,
    val searchResults: List<Restaurant> = emptyList(),
    val totalCount: Int = 0,
    val keyword: String = "",
    val searchBy: String = "all",
    val error: String? = null
)

@HiltViewModel
class SearchingViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val locationRepository: LocationRepository,
    private val networkErrorHandler: NetworkErrorHandler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private var _onNetworkError: (() -> Unit)? = null

    init {
        loadRecentSearches()
    }

    fun setNetworkErrorCallback(callback: () -> Unit) {
        _onNetworkError = callback
    }

    fun search(keyword: String, searchBy: String = "all") {
        if (keyword.isBlank()) return

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        error = null
                    )
                }

                val location = locationRepository.getCurrentLocation()
                if (location == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Không thể lấy vị trí của bạn"
                        )
                    }
                    _onNetworkError?.invoke()
                    return@launch
                }

                val result = networkErrorHandler.safeApiCall(
                    apiCall = {
                        restaurantRepository.searchRestaurants(
                            keyword = keyword,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            searchBy = searchBy
                        )
                    },
                    onNetworkError = { _onNetworkError?.invoke() }
                )

                result.fold(
                    onSuccess = { paginatedResult ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                searchResults = paginatedResult.data,
                                totalCount = paginatedResult.data.size,
                                keyword = keyword,
                                searchBy = searchBy,
                                error = null
                            )
                        }
                        // Save the search keyword for recent searches
                        saveRecentSearch(keyword)
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Có lỗi xảy ra khi tìm kiếm"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Có lỗi xảy ra khi tìm kiếm"
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(keyword = query) }
    }

    fun clearSearchResults() {
        _uiState.update { it.copy(searchResults = emptyList(), keyword = "") }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            _recentSearches.value = RecentSearchManager.getRecentSearches(context)
        }
    }

    private fun saveRecentSearch(keyword: String) {
        viewModelScope.launch {
            RecentSearchManager.saveSearch(context, keyword)
            loadRecentSearches()
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            RecentSearchManager.clearSearches(context)
            _recentSearches.value = emptyList()
        }
    }
}