package com.restaurant.app.network

import com.restaurant.app.model.MenuItemDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface for all restaurant-related API calls
 */
interface RestaurantApi {
    /**
     * Get menu items for a restaurant, optionally filtered by category
     * 
     * @param restaurantId The ID of the restaurant
     * @param categoryName Optional category name to filter menu items by
     * @return List of menu items
     */
    @GET("restaurants/{id}/menu-items")
    suspend fun getMenuItems(
        @Path("id") restaurantId: Long,
        @Query("categoryName") categoryName: String? = null
    ): Response<List<MenuItemDTO>>
    
    // Other restaurant-related API endpoints can be added here
}
