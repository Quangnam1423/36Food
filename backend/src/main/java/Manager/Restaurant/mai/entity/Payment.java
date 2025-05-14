package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal paymentAmount;
    private LocalDateTime paymentDate;
    private String paymentReference;
    private String paymentGateway;
}
