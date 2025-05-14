package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String message;
    private LocalDateTime notificationDate;
    private String status;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public Notification(Long notificationId, User user, String message, LocalDateTime notificationDate, String status) {
        this.notificationId = notificationId;
        this.user = user;
        this.message = message;
        this.notificationDate = notificationDate;
        this.status = status;
        this.isDeleted = false;
    }
}