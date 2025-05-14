package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserUserIdAndIsDeletedFalse(Long userId);
}


