package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "order_user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_payment_id", nullable = true)
    private Payment payment;

    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String orderStatus; // DRAFT, PENDING, PREPARING, DELIVERING, COMPLETED, CANCELLED
    
    private String note; // Ghi chú của người dùng về đơn hàng
    private String restaurantId; // ID của nhà hàng đặt món
    private BigDecimal itemsTotal; // Tổng giá trị các món
    private BigDecimal deliveryFee; // Phí giao hàng
    
    @Column(length = 500)
    private String restaurantAddress; // Địa chỉ nhà hàng (nơi giao)
    
    @Column(length = 500)
    private String customerAddress; // Địa chỉ khách hàng (nơi nhận)

    @ManyToOne
    @JoinColumn(name = "order_shipping_address", nullable = false)
    private Address shippingAddress;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderCreatedAt;
    private LocalDateTime orderUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;
    
    // Helper method to add item
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
    
    // Helper method to remove item
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
}
