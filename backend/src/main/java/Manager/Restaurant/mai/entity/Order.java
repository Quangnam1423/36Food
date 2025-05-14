package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private String orderStatus;

    @ManyToOne
    @JoinColumn(name = "order_shipping_address", nullable = false)
    private Address shippingAddress;

    private LocalDateTime orderCreatedAt;
    private LocalDateTime orderUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
