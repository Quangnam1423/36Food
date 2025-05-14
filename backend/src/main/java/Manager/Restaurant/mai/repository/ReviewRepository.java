package Manager.Restaurant.mai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Manager.Restaurant.mai.entity.Review;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Tìm kiếm review theo userId và isDeleted là false
    List<Review> findByUser_UserIdAndIsDeletedFalse(Long userId);

    // Tìm kiếm review theo restaurantId và isDeleted là false
    List<Review> findByRestaurant_IdAndIsDeletedFalse(Long restaurantId);

    // Tìm kiếm review theo menuItemId và isDeleted là false
    List<Review> findByFood_ItemIdAndIsDeletedFalse(Long itemId);
}
