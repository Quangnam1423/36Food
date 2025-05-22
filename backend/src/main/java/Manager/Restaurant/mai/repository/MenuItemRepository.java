package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.MenuItem;
import Manager.Restaurant.mai.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurant(Restaurant restaurant);
    List<MenuItem> findByRestaurantIdAndCategory(Long restaurantId, String category);
    
    // Tìm menu item theo tên chứa keyword (không phân biệt hoa thường)
    List<MenuItem> findByNameContainingIgnoreCase(String keyword);
    
    // Tìm danh sách nhà hàng có menu item có tên chứa keyword
    @Query("SELECT DISTINCT m.restaurant FROM MenuItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Restaurant> findRestaurantsByMenuItemNameContaining(@Param("keyword") String keyword);
}

