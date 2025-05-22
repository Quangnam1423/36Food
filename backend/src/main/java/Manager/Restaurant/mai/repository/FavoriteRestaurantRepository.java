package Manager.Restaurant.mai.repository;

import Manager.Restaurant.mai.entity.FavoriteRestaurant;
import Manager.Restaurant.mai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRestaurantRepository extends JpaRepository<FavoriteRestaurant, Long> {
    
    /**
     * Tìm tất cả nhà hàng yêu thích của một người dùng
     */
    List<FavoriteRestaurant> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Tìm tất cả nhà hàng yêu thích dựa trên ID người dùng
     */
    List<FavoriteRestaurant> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Kiểm tra xem nhà hàng có trong danh sách yêu thích của người dùng hay không
     */
    Optional<FavoriteRestaurant> findByUserAndRestaurantId(User user, String restaurantId);
    
    /**
     * Kiểm tra xem nhà hàng có trong danh sách yêu thích của người dùng hay không (sử dụng ID)
     */
    Optional<FavoriteRestaurant> findByUser_UserIdAndRestaurantId(Long userId, String restaurantId);
    
    /**
     * Xóa nhà hàng khỏi danh sách yêu thích của người dùng
     */
    void deleteByUserAndRestaurantId(User user, String restaurantId);
      /**
     * Xóa nhà hàng khỏi danh sách yêu thích của người dùng (sử dụng ID)
     */
    void deleteByUser_UserIdAndRestaurantId(Long userId, String restaurantId);
    
    /**
     * Tìm các nhà hàng được yêu thích nhiều nhất
     * Trả về danh sách chứa ID nhà hàng và số lượng yêu thích
     */
    @Query(value = 
           "SELECT restaurant_id, COUNT(restaurant_id) as favorite_count " +
           "FROM favorite_restaurants " +
           "GROUP BY restaurant_id " +
           "ORDER BY favorite_count DESC ",
           nativeQuery = true)
    List<Object[]> findMostFavoritedRestaurants();
}
