package Manager.Restaurant.mai.dto;

import java.util.List;
import java.util.Set;

import Manager.Restaurant.mai.entity.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseDTO {
    private List<MenuItem> menuItems;
    private Set<String> categories;
    private Long restaurantId;
    private String restaurantName;
}
