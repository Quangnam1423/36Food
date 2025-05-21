package Manager.Restaurant.mai.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private Double latitude;    // Vĩ độ của người dùng
    private Double longitude;   // Kinh độ của người dùng
}
