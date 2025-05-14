package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;

    // Gửi thông báo đến người dùng (lưu vào DB)
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        String message = data.get("message").toString();
        String status = data.getOrDefault("status", "UNREAD").toString();

        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().isDeleted()) {
            return ResponseEntity.badRequest().body("Người dùng không tồn tại hoặc đã bị xoá.");
        }

        Notification notification = new Notification();
        notification.setUser(userOpt.get());
        notification.setMessage(message);
        notification.setStatus(status);
        notification.setNotificationDate(LocalDateTime.now());
        notification.setDeleted(false);

        notificationRepo.save(notification);

        return ResponseEntity.ok(Map.of(
                "notificationId", notification.getNotificationId(),
                "message", "Thông báo đã được gửi."
        ));
    }
    // Gửi email thông báo đến người dùng
    @PostMapping("/email")
    public ResponseEntity<?> sendEmailNotification(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        String message = data.get("message").toString();
        String subject = data.getOrDefault("subject", "Thông báo từ hệ thống").toString();

        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().isDeleted()) {
            return ResponseEntity.badRequest().body("Người dùng không tồn tại hoặc đã bị xoá.");
        }

        User user = userOpt.get();
        String email = user.getUserEmail();

        System.out.printf("Gửi email đến %s\nTiêu đề: %s\nNội dung: %s\n", email, subject, message);

        return ResponseEntity.ok(Map.of(
                "to", email,
                "subject", subject,
                "message", message,
                "status", "Email đã được gửi (giả lập)"
        ));
    }
}

