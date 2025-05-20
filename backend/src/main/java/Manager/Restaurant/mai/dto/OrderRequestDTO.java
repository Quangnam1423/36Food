package Manager.Restaurant.mai.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private Long userId;
    private Long addressId;
    private Long voucherId;
    private String note; // Ghi chú của người dùng về đơn hàng
}
