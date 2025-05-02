package Manager.Restaurant.mai.repository;


import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<FoodItem, Long> {
}

