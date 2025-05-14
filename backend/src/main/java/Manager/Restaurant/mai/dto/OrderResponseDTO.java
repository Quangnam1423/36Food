package Manager.Restaurant.mai.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponseDTO {
    private Long orderId;
    private String orderStatus;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private BigDecimal totalAmount;
}
