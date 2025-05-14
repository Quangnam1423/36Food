package Manager.Restaurant.mai.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal paymentAmount;
    private LocalDateTime paymentDate;
    private String paymentReference;
    private String paymentGateway;
}
