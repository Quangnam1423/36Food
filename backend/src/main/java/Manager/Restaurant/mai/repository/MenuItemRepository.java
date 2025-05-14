package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}

