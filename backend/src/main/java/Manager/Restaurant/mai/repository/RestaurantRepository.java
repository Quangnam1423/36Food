package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // Tìm nhà hàng theo category chứa từ khóa cụ thể (không phân biệt chữ hoa/thường)
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.categories c WHERE LOWER(c) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Restaurant> findByCategoryContainingIgnoreCase(@Param("keyword") String keyword);
    
    // Tìm nhà hàng theo tên chứa từ khóa (không phân biệt chữ hoa/thường)
    List<Restaurant> findByNameContainingIgnoreCase(String keyword);
}

