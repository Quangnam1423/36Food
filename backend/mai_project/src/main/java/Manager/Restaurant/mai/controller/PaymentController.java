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
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;

    // POST /payment/process - Xử lý thanh toán
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> data) {
        Long orderId = Long.valueOf(data.get("orderId").toString());
        String method = data.getOrDefault("paymentMethod", "CASH").toString();
        String gateway = data.getOrDefault("paymentGateway", "manual").toString();
        String reference = data.getOrDefault("paymentReference", "REF-" + orderId).toString();

        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
        }

        Order order = orderOpt.get();

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

    // GET /payment/status/{order_id}
    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Long orderId) {
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        if (order.getPayment() == null) {
            return ResponseEntity.ok(Map.of("status", "UNPAID"));
        }

        return ResponseEntity.ok(Map.of(
                "paymentStatus", order.getPayment().getPaymentStatus(),
                "paymentMethod", order.getPayment().getPaymentMethod(),
                "amount", order.getPayment().getPaymentAmount()
        ));
    }

    // POST /payment/refund/{order_id}
    @PostMapping("/refund/{orderId}")
    public ResponseEntity<?> refundPayment(@PathVariable Long orderId) {
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        if (order.getPayment() == null || !"PAID".equals(order.getPayment().getPaymentStatus())) {
            return ResponseEntity.badRequest().body("Không thể hoàn tiền vì chưa thanh toán hoặc đã hoàn.");
        }

        order.getPayment().setPaymentStatus("REFUNDED");
        order.getPayment().setPaymentDate(LocalDateTime.now());
        paymentRepo.save(order.getPayment());

        order.setOrderStatus("REFUNDED");
        order.setOrderUpdatedAt(LocalDateTime.now());
        orderRepo.save(order);

        return ResponseEntity.ok(Map.of("refundStatus", "REFUNDED"));
    }
}
