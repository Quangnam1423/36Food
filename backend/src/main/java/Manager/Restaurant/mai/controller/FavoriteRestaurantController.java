package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.entity.FavoriteRestaurant;
import Manager.Restaurant.mai.service.FavoriteRestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteRestaurantController {    
    private final FavoriteRestaurantService favoriteRestaurantService;
    private final RestaurantController restaurantController;
    private final Manager.Restaurant.mai.repository.FavoriteRestaurantRepository favoriteRestaurantRepository;
    
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
     * API mới: Lấy danh sách nhà hàng yêu thích với đầy đủ thông tin chi tiết
     * Bao gồm: thông tin nhà hàng, khoảng cách, và thời gian giao hàng
     */
    @GetMapping("/restaurants")
    public ResponseEntity<?> getFavoriteRestaurantsWithDetails(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Double latitude,
            @RequestParam(required = false, defaultValue = "0") Double longitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            // Lấy danh sách ID nhà hàng yêu thích của người dùng
            List<FavoriteRestaurant> favorites = favoriteRestaurantService.getFavoriteRestaurants(userId);
            
            // Nếu không có nhà hàng yêu thích, trả về danh sách rỗng
            if (favorites.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "Bạn chưa có nhà hàng yêu thích nào",
                    "restaurants", List.of(),
                    "totalItems", 0,
                    "totalPages", 0,
                    "currentPage", page,
                    "pageSize", size,
                    "hasMore", false
                ));
            }
            
            // Lấy danh sách ID nhà hàng
            List<Long> restaurantIds = favorites.stream()
                    .map(fav -> Long.parseLong(fav.getRestaurantId()))
                    .collect(Collectors.toList());
            
            // Gọi API từ RestaurantController để lấy chi tiết các nhà hàng
            return restaurantController.getRestaurantsByIds(
                    request, restaurantIds, page, size, latitude, longitude);
                
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
    
    /**
     * API để lấy danh sách các nhà hàng được yêu thích nhiều nhất
     */
    @GetMapping("/most-favorited")
    public ResponseEntity<?> getMostFavoritedRestaurants(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Double latitude,
            @RequestParam(required = false, defaultValue = "0") Double longitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
            
            // Lấy danh sách nhà hàng được yêu thích nhiều nhất
            List<Object[]> mostFavoritedRestaurantIds = favoriteRestaurantRepository.findMostFavoritedRestaurants();
            
            // Chuyển đổi kết quả thành danh sách ID và số lượt yêu thích
            List<Map<String, Object>> result = new ArrayList<>();
            List<Long> restaurantIds = new ArrayList<>();
            
            for (Object[] row : mostFavoritedRestaurantIds) {
                String restaurantId = (String) row[0];
                Long favoriteCount = (Long) row[1];
                
                restaurantIds.add(Long.parseLong(restaurantId));
                
                Map<String, Object> item = new HashMap<>();
                item.put("restaurantId", restaurantId);
                item.put("favoriteCount", favoriteCount);
                result.add(item);
            }
            
            // Nếu không có kết quả, trả về danh sách rỗng
            if (restaurantIds.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "Chưa có nhà hàng nào được yêu thích",
                    "restaurants", List.of()
                ));
            }
            
            // Lấy thông tin chi tiết của các nhà hàng
            return restaurantController.getRestaurantsByIds(
                    request, restaurantIds, page, size, latitude, longitude);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi lấy danh sách nhà hàng được yêu thích nhiều nhất: " + e.getMessage());
        }
    }
    
    /**
     * API để lấy tất cả nhà hàng yêu thích mà không phân trang
     * Trả về danh sách đầy đủ với thông tin chi tiết của nhà hàng
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllFavoriteRestaurants(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0") Double latitude,
            @RequestParam(required = false, defaultValue = "0") Double longitude
    ) {
        try {
            // Lấy userId từ token JWT
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            // Lấy danh sách nhà hàng yêu thích của người dùng
            List<FavoriteRestaurant> favorites = favoriteRestaurantService.getFavoriteRestaurants(userId);
            
            // Nếu không có nhà hàng yêu thích, trả về danh sách rỗng
            if (favorites.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "Bạn chưa có nhà hàng yêu thích nào",
                    "restaurants", List.of()
                ));
            }
            
            // Lấy danh sách ID nhà hàng
            List<Long> restaurantIds = favorites.stream()
                    .map(fav -> Long.parseLong(fav.getRestaurantId()))
                    .collect(Collectors.toList());
            
            // Gọi API từ RestaurantController để lấy chi tiết tất cả nhà hàng không phân trang
            return restaurantController.getAllRestaurantsByIds(
                    request, restaurantIds, latitude, longitude);
                
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi lấy danh sách nhà hàng yêu thích: " + e.getMessage());
        }
    }
}
