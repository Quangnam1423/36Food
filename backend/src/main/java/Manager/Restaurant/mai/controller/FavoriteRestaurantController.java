package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.entity.FavoriteRestaurant;
import Manager.Restaurant.mai.service.FavoriteRestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteRestaurantController {    private final FavoriteRestaurantService favoriteRestaurantService;
    private final RestaurantController restaurantController;
    
    /**
     * Lấy danh sách nhà hàng yêu thích của người dùng với phân trang
     */
    @GetMapping
    public ResponseEntity<?> getFavoriteRestaurants(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean includeDetails
    ) {
        try {
            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            // Lấy danh sách yêu thích với phân trang
            Map<String, Object> result = favoriteRestaurantService.getFavoriteRestaurantsWithPagination(
                userId, page, size, includeDetails
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi lấy danh sách nhà hàng yêu thích: " + e.getMessage());
        }
    }
      /**
     * Kiểm tra nhà hàng có trong danh sách yêu thích không
     */
    @GetMapping("/check/{restaurantId}")
    public ResponseEntity<?> checkFavoriteStatus(
            HttpServletRequest request,
            @PathVariable String restaurantId
    ) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            boolean isFavorite = favoriteRestaurantService.isFavorite(userId, restaurantId);
            
            return ResponseEntity.ok(Map.of(
                "isFavorite", isFavorite
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi kiểm tra trạng thái yêu thích: " + e.getMessage());
        }
    }
    
    /**
     * Thêm nhà hàng vào danh sách yêu thích
     */
    @PostMapping("/{restaurantId}")
    public ResponseEntity<?> addFavoriteRestaurant(
            HttpServletRequest request,
            @PathVariable String restaurantId
    ) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            FavoriteRestaurant favorite = favoriteRestaurantService.addFavoriteRestaurant(userId, restaurantId);
            
            return ResponseEntity.ok(Map.of(
                "id", favorite.getId(),
                "restaurantId", favorite.getRestaurantId(),
                "isFavorite", true,
                "message", "Đã thêm nhà hàng vào danh sách yêu thích"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi thêm nhà hàng vào danh sách yêu thích: " + e.getMessage());
        }
    }
    
    /**
     * Xóa nhà hàng khỏi danh sách yêu thích
     */
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<?> removeFavoriteRestaurant(
            HttpServletRequest request,
            @PathVariable String restaurantId
    ) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            favoriteRestaurantService.removeFavoriteRestaurant(userId, restaurantId);
            
            return ResponseEntity.ok(Map.of(
                "isFavorite", false,
                "message", "Đã xóa nhà hàng khỏi danh sách yêu thích"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi xóa nhà hàng khỏi danh sách yêu thích: " + e.getMessage());
        }
    }
    
    /**
     * Toggle (thêm/xóa) nhà hàng trong danh sách yêu thích
     */
    @PostMapping("/toggle/{restaurantId}")
    public ResponseEntity<?> toggleFavoriteRestaurant(
            HttpServletRequest request,
            @PathVariable String restaurantId
    ) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            boolean isAdded = favoriteRestaurantService.toggleFavoriteRestaurant(userId, restaurantId);
            
            if (isAdded) {
                return ResponseEntity.ok(Map.of(
                    "isFavorite", true,
                    "message", "Đã thêm nhà hàng vào danh sách yêu thích"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "isFavorite", false,
                    "message", "Đã xóa nhà hàng khỏi danh sách yêu thích"
                ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi thao tác với danh sách yêu thích: " + e.getMessage());
        }
    }
}
