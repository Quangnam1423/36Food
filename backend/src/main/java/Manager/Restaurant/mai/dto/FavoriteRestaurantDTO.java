package Manager.Restaurant.mai.dto;

import Manager.Restaurant.mai.entity.FavoriteRestaurant;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRestaurantDTO {
    private Long id;
    private String restaurantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private RestaurantDTO restaurant; // Chi tiết nhà hàng (nếu cần)
    
    /**
     * Tạo DTO từ entity FavoriteRestaurant
     */
    public static FavoriteRestaurantDTO fromEntity(FavoriteRestaurant favoriteRestaurant) {
        return FavoriteRestaurantDTO.builder()
                .id(favoriteRestaurant.getId())
                .restaurantId(favoriteRestaurant.getRestaurantId())
                .createdAt(favoriteRestaurant.getCreatedAt())
                .updatedAt(favoriteRestaurant.getUpdatedAt())
                .build();
    }
    
    /**
     * Tạo DTO từ entity FavoriteRestaurant và RestaurantDTO
     */
    public static FavoriteRestaurantDTO fromEntityWithRestaurant(
            FavoriteRestaurant favoriteRestaurant, 
            RestaurantDTO restaurantDTO) {
        
        return FavoriteRestaurantDTO.builder()
                .id(favoriteRestaurant.getId())
                .restaurantId(favoriteRestaurant.getRestaurantId())
                .createdAt(favoriteRestaurant.getCreatedAt())
                .updatedAt(favoriteRestaurant.getUpdatedAt())
                .restaurant(restaurantDTO)
                .build();
    }
}
