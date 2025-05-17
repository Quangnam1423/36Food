package Manager.Restaurant.mai.dto;

import Manager.Restaurant.mai.entity.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private boolean isAvailable;  // Thêm trường isAvailable
    private Integer saleCount;    // Thêm trường saleCount
    private Integer likes;        // Thêm trường likes
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long restaurantId;  // Added restaurant ID for more context

    
    public static MenuItemDTO fromEntity(MenuItem menuItem) {
        return MenuItemDTO.builder()
            .id(menuItem.getItemId())
            .name(menuItem.getName())
            .description(menuItem.getDescription())
            .price(menuItem.getPrice())
            .category(menuItem.getCategory())
            .imageUrl(menuItem.getImageUrl())
            .isAvailable(menuItem.isAvailable())  // Thêm trường isAvailable
            .saleCount(menuItem.getSaleCount())   // Thêm trường saleCount
            .likes(menuItem.getLikes())           // Thêm trường likes
            .createdAt(menuItem.getCreatedAt())
            .updatedAt(menuItem.getUpdatedAt())
            .restaurantId(menuItem.getRestaurant() != null ? menuItem.getRestaurant().getId() : null)
            .build();
    }
}
