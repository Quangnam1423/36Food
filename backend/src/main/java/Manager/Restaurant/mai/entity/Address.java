package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "address")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adrId;

    private String adr;

    @ManyToOne
    @JoinColumn(name = "adr_user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public Address(String adr, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.adr = adr;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = false;
    }
}