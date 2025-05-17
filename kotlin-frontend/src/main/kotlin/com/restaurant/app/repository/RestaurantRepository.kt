package com.restaurant.app.repository

import com.restaurant.app.model.MenuItemDTO
import com.restaurant.app.network.RestaurantApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for handling restaurant data operations
 */
class RestaurantRepository(private val restaurantApi: RestaurantApi) {
    
    /**
     * Get menu items for a restaurant, optionally filtered by category
     * 
     * @param restaurantId The ID of the restaurant
     * @param categoryName Optional category name to filter by, null to get all items
     * @return List of menu items if successful, empty list otherwise
     */
    suspend fun getMenuItems(restaurantId: Long, categoryName: String? = null): List<MenuItemDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val response = restaurantApi.getMenuItems(restaurantId, categoryName)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                // Log error
                println("Error fetching menu items: ${e.message}")
                emptyList()
            }
        }
    }
}
