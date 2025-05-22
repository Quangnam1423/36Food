package Manager.Restaurant.mai.dto;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import Manager.Restaurant.mai.entity.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantDTO {    private Long id;
    private String name;
    private String imageUrl;
    private Float rating;
    private Integer ratingCount;    
    private String address;
    private String priceRange;
    private String openingStatus;
    private String businessHours;
    private String phoneNumber;
    private Integer likes;
    private Integer reviewsCount;
    private Double distance;    
    private List<String> categories;  // Category names
    private Map<String, List<MenuItemDTO>> menuItemsByCategory; // Menu items grouped by category
    private Long createdAt;
    private Integer durationInMinutes;
    private Long orderCount; // Số lượng đơn hàng đã bán
    private Boolean isFavorite; // Đánh dấu nhà hàng có đang được yêu thích hay không
    
    /**
     * Tạo RestaurantDTO từ entity Restaurant bao gồm cả danh sách categories
     */
    public static RestaurantDTO fromEntity(Restaurant res, String address, double distanceInMeters, double durationInSeconds) {
        // Use the categories directly from the Restaurant entity
        List<String> categoryNames = res.getCategories() != null ? 
            new ArrayList<>(res.getCategories()) : 
            List.of();
            
        return RestaurantDTO.builder()
                .id(res.getId())
                .name(res.getName())
                .imageUrl(res.getImageUrl())
                .rating(res.getRating())
                .ratingCount(res.getRatingCount())
                .address(address)
                .priceRange(res.getPriceRange())
                .openingStatus(res.getOpeningStatus())
                .businessHours(res.getBusinessHours())
                .phoneNumber(res.getPhoneNumber())
                .likes(res.getLikes())
                .reviewsCount(res.getReviewsCount())
                .distance(distanceInMeters / 1000.0)                .categories(categoryNames)
                .menuItemsByCategory(null) // Will be set when needed
                .createdAt(res.getCreatedAt())
                .durationInMinutes((int)durationInSeconds / 60)
                .orderCount(null) // Will be set when needed
                .isFavorite(false) // Default is not favorite, will be updated if needed
                .build();
    }
    
    /**
     * Phương thức tiện ích để tạo một RestaurantDTO với danh sách menu items được phân loại theo category
     */
    public static RestaurantDTO fromEntityWithMenuItems(Restaurant res, String address, double distanceInMeters, 
                                                       double durationInSeconds, Map<String, List<MenuItemDTO>> menuItemsByCategory) {
        RestaurantDTO dto = fromEntity(res, address, distanceInMeters, durationInSeconds);
        dto.setMenuItemsByCategory(menuItemsByCategory);
        return dto;
    }
}
