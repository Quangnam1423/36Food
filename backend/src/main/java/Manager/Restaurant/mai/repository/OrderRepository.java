package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserUserIdAndIsDeletedFalse(Long userId);
    List<Order> findByUser_UserIdAndOrderStatusOrderByOrderDateDesc(Long userId, String status);
    List<Order> findByUser_UserIdOrderByOrderDateDesc(Long userId);
    
    // Tìm đơn hàng theo trạng thái và khoảng thời gian
    List<Order> findByOrderStatusAndOrderDateBetweenOrderByOrderDateDesc(
        String status, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    // Tìm đơn hàng theo khoảng thời gian (không lọc theo trạng thái)
    List<Order> findByOrderDateBetweenOrderByOrderDateDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    // Tìm đơn hàng của người dùng theo trạng thái và khoảng thời gian
    List<Order> findByUser_UserIdAndOrderStatusAndOrderDateBetweenOrderByOrderDateDesc(
        Long userId,
        String status, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    // Tìm đơn hàng của người dùng theo khoảng thời gian (không lọc theo trạng thái)
    List<Order> findByUser_UserIdAndOrderDateBetweenOrderByOrderDateDesc(
        Long userId,
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    // Tìm đơn hàng của người dùng theo danh sách trạng thái
    List<Order> findByUser_UserIdAndOrderStatusInOrderByOrderDateDesc(
        Long userId,
        List<String> statuses
    );
    
    // Tìm đơn hàng của người dùng theo danh sách trạng thái có phân trang
    Page<Order> findByUser_UserIdAndOrderStatusInOrderByOrderDateDesc(
        Long userId,
        List<String> statuses,
        Pageable pageable
    );
    
    // Tìm đơn hàng của người dùng không bao gồm các trạng thái nhất định có phân trang
    Page<Order> findByUser_UserIdAndOrderStatusNotInOrderByOrderDateDesc(
        Long userId,
        List<String> statuses,
        Pageable pageable
    );
}


