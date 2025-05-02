package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

