package Manager.Restaurant.mai.repository;

import Manager.Restaurant.mai.entity.Category;
import Manager.Restaurant.mai.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByRestaurant(Restaurant restaurant);
    
    List<Category> findByRestaurantId(Long restaurantId);
}
