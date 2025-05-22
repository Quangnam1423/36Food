package Manager.Restaurant.mai.service;

import Manager.Restaurant.mai.dto.FavoriteRestaurantDTO;
import Manager.Restaurant.mai.dto.RestaurantDTO;
import Manager.Restaurant.mai.entity.FavoriteRestaurant;
import Manager.Restaurant.mai.entity.Restaurant;
import Manager.Restaurant.mai.entity.User;
import Manager.Restaurant.mai.repository.FavoriteRestaurantRepository;
import Manager.Restaurant.mai.repository.RestaurantRepository;
import Manager.Restaurant.mai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteRestaurantService {    private final FavoriteRestaurantRepository favoriteRestaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;

    /**
     * Lấy danh sách nhà hàng yêu thích của người dùng
     */
    public List<FavoriteRestaurant> getFavoriteRestaurants(Long userId) {
        return favoriteRestaurantRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Lấy danh sách nhà hàng yêu thích của người dùng với phân trang
     * và tùy chọn bao gồm chi tiết nhà hàng
     */
    public Map<String, Object> getFavoriteRestaurantsWithPagination(
            Long userId, int page, int size, boolean includeDetails) {
        
        // Lấy tất cả nhà hàng yêu thích của người dùng
        List<FavoriteRestaurant> allFavorites = favoriteRestaurantRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        
        // Tính toán thông tin phân trang
        int totalItems = allFavorites.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        
        // Giới hạn page trong phạm vi hợp lệ
        if (page < 0) page = 0;
        if (page >= totalPages && totalPages > 0) page = totalPages - 1;
        
        // Tính vị trí bắt đầu và kết thúc cho phân trang
        int startItem = page * size;
        int endItem = Math.min(startItem + size, totalItems);
        
        List<FavoriteRestaurant> pagedFavorites;
        if (startItem < totalItems) {
            pagedFavorites = allFavorites.subList(startItem, endItem);
        } else {
            pagedFavorites = List.of();
        }
        
        Map<String, Object> response = new HashMap<>();
        
        if (includeDetails) {
            // Chuyển đổi sang DTO với chi tiết nhà hàng
            List<FavoriteRestaurantDTO> favoriteDTOs = pagedFavorites.stream()
                .map(favorite -> {
                    FavoriteRestaurantDTO dto = new FavoriteRestaurantDTO();
                    dto.setId(favorite.getId());
                    dto.setRestaurantId(favorite.getRestaurantId());
                    dto.setCreatedAt(favorite.getCreatedAt());
                    dto.setUpdatedAt(favorite.getUpdatedAt());
                    
                    // Lấy thông tin chi tiết nhà hàng
                    try {
                        // Chuyển String restaurantId sang Long nếu cần
                        Long restaurantId = Long.parseLong(favorite.getRestaurantId());
                        dto.setRestaurant(getRestaurantDetails(restaurantId));
                    } catch (IllegalArgumentException e) {
                        // Log lỗi nhưng vẫn trả về thông tin cơ bản
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
            
            response.put("favorites", favoriteDTOs);
        } else {
            // Chỉ trả về danh sách ID nhà hàng
            List<String> restaurantIds = pagedFavorites.stream()
                .map(FavoriteRestaurant::getRestaurantId)
                .collect(Collectors.toList());
            
            response.put("favoriteRestaurantIds", restaurantIds);
        }
        
        // Thêm thông tin phân trang
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("hasMore", page < totalPages - 1);
        
        return response;
    }
    
    /**
     * Lấy thông tin chi tiết của nhà hàng
     */
    private RestaurantDTO getRestaurantDetails(Long restaurantId) {
        // Lấy thông tin nhà hàng từ service
        try {
            // Nếu cần lấy thông tin chi tiết với địa chỉ và khoảng cách, cần truyền thêm vị trí người dùng
            // Ở đây chỉ lấy thông tin cơ bản
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            
            // Xây dựng đối tượng DTO chỉ với thông tin cơ bản
            return RestaurantDTO.builder()
                    .id(restaurant.getId())
                    .name(restaurant.getName())
                    .imageUrl(restaurant.getImageUrl())
                    .rating(restaurant.getRating())
                    .ratingCount(restaurant.getRatingCount())
                    .priceRange(restaurant.getPriceRange())
                    .openingStatus(restaurant.getOpeningStatus())
                    .businessHours(restaurant.getBusinessHours())
                    .phoneNumber(restaurant.getPhoneNumber())
                    .likes(restaurant.getLikes())
                    .reviewsCount(restaurant.getReviewsCount())
                    .categories(restaurant.getCategories())
                    .createdAt(restaurant.getCreatedAt())
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Không thể lấy thông tin nhà hàng: " + e.getMessage());
        }
    }

    /**
     * Thêm nhà hàng vào danh sách yêu thích
     */
    @Transactional
    public FavoriteRestaurant addFavoriteRestaurant(Long userId, String restaurantId) {
        // Kiểm tra người dùng tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        // Kiểm tra xem nhà hàng đã có trong danh sách yêu thích chưa
        Optional<FavoriteRestaurant> existingFavorite = 
                favoriteRestaurantRepository.findByUserAndRestaurantId(user, restaurantId);
        
        if (existingFavorite.isPresent()) {
            return existingFavorite.get(); // Nhà hàng đã có trong danh sách yêu thích
        }

        // Kiểm tra xem nhà hàng có tồn tại không (tùy vào cách lưu trữ nhà hàng)
        // Nếu nhà hàng được lưu trong database, thêm logic kiểm tra ở đây
        
        // Tạo mục yêu thích mới
        FavoriteRestaurant favoriteRestaurant = FavoriteRestaurant.builder()
                .user(user)
                .restaurantId(restaurantId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return favoriteRestaurantRepository.save(favoriteRestaurant);
    }

    /**
     * Kiểm tra xem nhà hàng có trong danh sách yêu thích của người dùng không
     */
    public boolean isFavorite(Long userId, String restaurantId) {
        return favoriteRestaurantRepository.findByUser_UserIdAndRestaurantId(userId, restaurantId).isPresent();
    }

    /**
     * Xóa nhà hàng khỏi danh sách yêu thích
     */
    @Transactional
    public void removeFavoriteRestaurant(Long userId, String restaurantId) {
        // Kiểm tra người dùng tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        // Xóa mục yêu thích
        favoriteRestaurantRepository.deleteByUserAndRestaurantId(user, restaurantId);
    }

    /**
     * Toggle (thêm/xóa) nhà hàng yêu thích
     */
    @Transactional
    public boolean toggleFavoriteRestaurant(Long userId, String restaurantId) {
        if (isFavorite(userId, restaurantId)) {
            // Nếu đã yêu thích, xóa khỏi danh sách
            removeFavoriteRestaurant(userId, restaurantId);
            return false; // Trả về false để biết đã xóa khỏi danh sách
        } else {
            // Nếu chưa yêu thích, thêm vào danh sách
            addFavoriteRestaurant(userId, restaurantId);
            return true; // Trả về true để biết đã thêm vào danh sách
        }
    }
}
