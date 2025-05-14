package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.dto.OrderRequestDTO;
import Manager.Restaurant.mai.dto.OrderResponseDTO;
import Manager.Restaurant.mai.dto.PaymentDTO;
import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.service.CartService;
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
    private final VoucherRepository voucherRepo;
    private final CartService cartService;


    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO dto) {
        Optional<User> userOpt = userRepo.findById(dto.getUserId());
        Optional<Address> addressOpt = addressRepo.findById(dto.getAddressId());

        if (userOpt.isEmpty() || addressOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Thông tin người dùng hoặc địa chỉ không hợp lệ!");
        }

        User user = userOpt.get();
        Address address = addressOpt.get();

        if (user.isDeleted() || address.isDeleted()) {
            return ResponseEntity.badRequest().body("Người dùng hoặc địa chỉ đã bị xoá!");
        }

        // Lấy giỏ hàng hiện tại
        Cart cart = cartService.getCartByUser(user.getUserId());
        if (cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Giỏ hàng đang trống, không thể tạo đơn hàng!");
        }

        // Tính tổng tiền từ giỏ hàng
        double totalPrice = cart.getTotalPrice();

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setTotalAmount(BigDecimal.valueOf(totalPrice));
        order.setOrderStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        order.setOrderCreatedAt(LocalDateTime.now());
        order.setOrderUpdatedAt(LocalDateTime.now());
        order.setDeleted(false);
        order.setPayment(null);

        if (dto.getVoucherId() != null) {
            voucherRepo.findById(dto.getVoucherId()).ifPresent(order::setVoucher);
        }

        Order savedOrder = orderRepo.save(order);

        // Xoá giỏ hàng sau khi đặt đơn
        cartService.clearCart(user.getUserId());

        OrderResponseDTO response = OrderResponseDTO.builder()
                .orderId(savedOrder.getOrderId())
                .orderStatus(savedOrder.getOrderStatus())
                .orderDate(savedOrder.getOrderDate())
                .totalAmount(savedOrder.getTotalAmount())
                .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderStatus(@PathVariable Long id) {
        return orderRepo.findById(id)
                .filter(order -> !order.isDeleted())
                .map(order -> ResponseEntity.ok(Map.of(
                        "orderId", order.getOrderId(),
                        "status", order.getOrderStatus(),
                        "updatedAt", order.getOrderUpdatedAt()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        return orderRepo.findById(id)
                .filter(order -> !order.isDeleted())
                .map(order -> {
                    if ("CANCELLED".equalsIgnoreCase(order.getOrderStatus())) {
                        return ResponseEntity.badRequest().body("Đơn hàng đã bị hủy trước đó.");
                    }
                    if (order.getPayment() != null &&
                            "PAID".equalsIgnoreCase(order.getPayment().getPaymentStatus())) {
                        return ResponseEntity.badRequest().body("Không thể hủy đơn đã thanh toán.");
                    }

                    order.setOrderStatus("CANCELLED");
                    order.setOrderUpdatedAt(LocalDateTime.now());
                    Order cancelledOrder = orderRepo.save(order);

                    OrderResponseDTO response = OrderResponseDTO.builder()
                            .orderId(cancelledOrder.getOrderId())
                            .orderStatus(cancelledOrder.getOrderStatus())
                            .orderDate(cancelledOrder.getOrderDate())
                            .updatedAt(cancelledOrder.getOrderUpdatedAt())
                            .totalAmount(cancelledOrder.getTotalAmount())
                            .build();

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Cập nhật trạng thái đơn hàng//
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
        }

        Order order = orderOpt.get();
        order.setOrderStatus(status.toUpperCase());
        order.setOrderUpdatedAt(LocalDateTime.now());

        orderRepo.save(order);

        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công.");
    }


}
