package Manager.Restaurant.mai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderRequestDTO {
    private Long orderId;
    private Double latitude;
    private Double longitude;
}
