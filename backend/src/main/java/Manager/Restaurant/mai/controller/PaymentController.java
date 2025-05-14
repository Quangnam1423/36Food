package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;

    // POST /payment/process - Xử lý thanh toán
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> data) {
        Long orderId;
        try {
            orderId = Long.valueOf(data.get("orderId").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Thiếu hoặc sai định dạng orderId.");
        }

        String method = data.getOrDefault("paymentMethod", "CASH").toString();
        String gateway = data.getOrDefault("paymentGateway", "manual").toString();
        String reference = data.getOrDefault("paymentReference", "REF-" + orderId).toString();

        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
        }

        Order order = orderOpt.get();
        if (order.isDeleted()) {
            return ResponseEntity.badRequest().body("Đơn hàng đã bị xoá.");
        }

        if ("PAID".equalsIgnoreCase(order.getOrderStatus())) {
            return ResponseEntity.badRequest().body("Đơn hàng đã được thanh toán.");
        }

        Payment payment = new Payment();
        payment.setPaymentMethod(method);
        payment.setPaymentStatus("PAID");
        payment.setPaymentAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentReference(reference);
        payment.setPaymentGateway(gateway);

        Payment savedPayment = paymentRepo.save(payment);

        order.setPayment(savedPayment);
        order.setOrderStatus("PAID");
        order.setOrderUpdatedAt(LocalDateTime.now());
        orderRepo.save(order);

        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "paymentStatus", "PAID",
                "paymentId", savedPayment.getPaymentId()
        ));
    }

    // GET /payment/status/{orderId}
    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Long orderId) {
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        if (order.isDeleted()) {
            return ResponseEntity.badRequest().body("Đơn hàng đã bị xoá.");
        }

        Payment payment = order.getPayment();
        if (payment == null) {
            return ResponseEntity.ok(Map.of("status", "UNPAID"));
        }

        return ResponseEntity.ok(Map.of(
                "paymentStatus", payment.getPaymentStatus(),
                "paymentMethod", payment.getPaymentMethod(),
                "amount", payment.getPaymentAmount()
        ));
    }

    // POST /payment/refund/{orderId}
    @PostMapping("/refund/{orderId}")
    public ResponseEntity<?> refundPayment(@PathVariable("orderId") Long orderId) {
        if (orderId == null) {
            return ResponseEntity.badRequest().body("orderId không được để trống.");
        }

        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy đơn hàng.");
        }

        Order order = orderOpt.get();

        if (order.isDeleted()) {
            return ResponseEntity.badRequest().body("Đơn hàng đã bị xoá.");
        }

        Payment payment = order.getPayment();
        if (payment == null) {
            return ResponseEntity.badRequest().body("Đơn hàng chưa được thanh toán.");
        }

        if ("REFUNDED".equalsIgnoreCase(payment.getPaymentStatus())) {
            return ResponseEntity.badRequest().body("Đơn hàng đã được hoàn tiền trước đó.");
        }

        if (!"SUCCESS".equalsIgnoreCase(payment.getPaymentStatus())) {
            return ResponseEntity.badRequest().body("Không thể hoàn tiền cho đơn hàng chưa thanh toán.");
        }

        // Cập nhật trạng thái hoàn tiền
        payment.setPaymentStatus("REFUNDED");
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepo.save(payment);

        order.setOrderStatus("REFUNDED");
        order.setOrderUpdatedAt(LocalDateTime.now());
        orderRepo.save(order);

        return ResponseEntity.ok(Map.of(
                "orderId", order.getOrderId(),
                "refundStatus", "REFUNDED",
                "refundedAt", payment.getPaymentDate()
        ));
    }
}
