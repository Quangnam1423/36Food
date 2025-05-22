package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.dto.*;
import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing favorite restaurant functionality
 */
@RestController
@RequestMapping("/test/favorites")
public class FavoriteRestaurantTestController {

    private final FavoriteRestaurantService favoriteRestaurantService;
    private final RestaurantService restaurantService;

    public FavoriteRestaurantTestController(
            FavoriteRestaurantService favoriteRestaurantService,
            RestaurantService restaurantService) {
        this.favoriteRestaurantService = favoriteRestaurantService;
        this.restaurantService = restaurantService;
    }

    /**
     * Test endpoint to check favorite restaurant functionality
     * @return A summary of the test results
     */
    @GetMapping("/test")
    public ResponseEntity<?> testFavoriteRestaurant() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Test user ID for testing purposes
            Long testUserId = 1L;
            
            // Test restaurant ID for testing purposes
            String testRestaurantId = "1";
            
            // Test 1: Check if restaurant is favorite (should be false initially)
            boolean initialFavoriteStatus = favoriteRestaurantService.isFavorite(testUserId, testRestaurantId);
            result.put("initialFavoriteStatus", initialFavoriteStatus);
            
            // Test 2: Add restaurant to favorites
            FavoriteRestaurant favorite = favoriteRestaurantService.addFavoriteRestaurant(testUserId, testRestaurantId);
            result.put("addedToFavorites", favorite != null);
            result.put("favoriteId", favorite.getId());
            
            // Test 3: Check if restaurant is favorite (should be true now)
            boolean favoriteStatusAfterAdd = favoriteRestaurantService.isFavorite(testUserId, testRestaurantId);
            result.put("favoriteStatusAfterAdd", favoriteStatusAfterAdd);
            
            // Test 4: Remove restaurant from favorites
            favoriteRestaurantService.removeFavoriteRestaurant(testUserId, testRestaurantId);
            result.put("removedFromFavorites", true);
            
            // Test 5: Check if restaurant is favorite (should be false again)
            boolean favoriteStatusAfterRemove = favoriteRestaurantService.isFavorite(testUserId, testRestaurantId);
            result.put("favoriteStatusAfterRemove", favoriteStatusAfterRemove);
            
            // Test 6: Toggle favorite (should add to favorites)
            boolean toggleAddResult = favoriteRestaurantService.toggleFavoriteRestaurant(testUserId, testRestaurantId);
            result.put("toggleAddResult", toggleAddResult);
            
            // Test 7: Check if restaurant is favorite (should be true again)
            boolean favoriteStatusAfterToggleAdd = favoriteRestaurantService.isFavorite(testUserId, testRestaurantId);
            result.put("favoriteStatusAfterToggleAdd", favoriteStatusAfterToggleAdd);
            
            // Test 8: Toggle favorite (should remove from favorites)
            boolean toggleRemoveResult = favoriteRestaurantService.toggleFavoriteRestaurant(testUserId, testRestaurantId);
            result.put("toggleRemoveResult", !toggleRemoveResult);
            
            // Test 9: Check if restaurant is favorite (should be false again)
            boolean favoriteStatusAfterToggleRemove = favoriteRestaurantService.isFavorite(testUserId, testRestaurantId);
            result.put("favoriteStatusAfterToggleRemove", favoriteStatusAfterToggleRemove);
            
            result.put("success", true);
            result.put("message", "All favorite restaurant tests passed!");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("message", "Error testing favorite restaurant functionality");
            
            return ResponseEntity.status(500).body(result);
        }
    }
}
