package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final AddressRepository addressRepo;
    private final PaymentRepository paymentRepo;
    private final VoucherRepository voucherRepo;

    // POST /orders - Tạo đơn hàng
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        Long addressId = Long.valueOf(data.get("addressId").toString());
        BigDecimal amount = new BigDecimal(data.get("totalAmount").toString());
        String status = data.getOrDefault("orderStatus", "PENDING").toString();
        Long voucherId = data.get("voucherId") != null ? Long.valueOf(data.get("voucherId").toString()) : null;

        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Address> addressOpt = addressRepo.findById(addressId);

        if (userOpt.isEmpty() || addressOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Thông tin người dùng hoặc địa chỉ không hợp lệ!");
        }

        Order order = new Order();
        order.setUser(userOpt.get());
        order.setShippingAddress(addressOpt.get());
        order.setTotalAmount(amount);
        order.setOrderStatus(status);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderCreatedAt(LocalDateTime.now());
        order.setOrderUpdatedAt(LocalDateTime.now());

        if (voucherId != null) {
            voucherRepo.findById(voucherId).ifPresent(order::setVoucher);
        }

        return ResponseEntity.ok(orderRepo.save(order));
    }

    // GET /orders/{id} - Xem trạng thái đơn hàng
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderStatus(@PathVariable Long id) {
        return orderRepo.findById(id)
                .map(order -> ResponseEntity.ok(Map.of(
                        "orderId", order.getOrderId(),
                        "status", order.getOrderStatus(),
                        "updatedAt", order.getOrderUpdatedAt()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /orders/{id}/cancel - Hủy đơn hàng
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        Optional<Order> orderOpt = orderRepo.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        if ("CANCELLED".equalsIgnoreCase(order.getOrderStatus())) {
            return ResponseEntity.badRequest().body("Đơn hàng đã bị hủy trước đó.");
        }

        order.setOrderStatus("CANCELLED");
        order.setOrderUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(orderRepo.save(order));
    }
}
