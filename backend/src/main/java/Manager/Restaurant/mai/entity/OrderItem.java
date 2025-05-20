package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
    private String note;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    private String menuItemId;
    private String restaurantId;
    
    // Phương thức tiện ích để tính tổng giá trị của item này
    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
