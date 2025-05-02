package Manager.Restaurant.mai.repository;

import Manager.Restaurant.mai.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    void deleteByUserUserId(Long userId);
}

