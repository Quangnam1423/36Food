package Manager.Restaurant.mai.repository;

import Manager.Restaurant.mai.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUserIdAndIsDeletedFalse(Long userId);
    void deleteByUserUserId(Long userId);
}