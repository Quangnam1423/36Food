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
public class BasicMenuItemDTO {
    private Long id;  // Changed from itemId to id to match builder usage
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static BasicMenuItemDTO fromEntity(MenuItem menuItem) {
        return new BasicMenuItemDTO(
            menuItem.getItemId(),
            menuItem.getName(),
            menuItem.getDescription(),
            menuItem.getPrice(),
            menuItem.getCategory(),
            menuItem.getImageUrl(),
            menuItem.getCreatedAt(),
            menuItem.getUpdatedAt()
        );
    }
}
