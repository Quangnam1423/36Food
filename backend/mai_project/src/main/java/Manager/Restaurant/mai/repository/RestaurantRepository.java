package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}

