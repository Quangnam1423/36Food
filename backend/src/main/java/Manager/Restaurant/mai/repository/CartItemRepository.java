package Manager.Restaurant.mai.repository;

import Manager.Restaurant.mai.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, String> {}

