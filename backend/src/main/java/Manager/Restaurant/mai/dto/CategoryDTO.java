package Manager.Restaurant.mai.dto;

import Manager.Restaurant.mai.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private Long restaurantId;
    
    public static CategoryDTO fromEntity(Category category) {
        return new CategoryDTO(
            category.getId(),
            category.getName(),
            category.getRestaurant().getId()
        );
    }
}
