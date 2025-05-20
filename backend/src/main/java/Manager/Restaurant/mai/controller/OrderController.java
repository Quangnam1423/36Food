package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.dto.OrderRequestDTO;
import Manager.Restaurant.mai.dto.OrderResponseDTO;
import Manager.Restaurant.mai.dto.PaymentDTO;
import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.service.CartService;
import Manager.Restaurant.mai.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final AddressRepository addressRepo;
    private final VoucherRepository voucherRepo;
    private final CartService cartService;
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO dto) {
        try {
            Order order = orderService.placeOrder(dto.getUserId(), dto.getAddressId(), dto.getNote());
            
            OrderResponseDTO response = OrderResponseDTO.builder()
                    .orderId(order.getOrderId())
                    .orderStatus(order.getOrderStatus())
                    .orderDate(order.getOrderDate())
                    .totalAmount(order.getTotalAmount())
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) String status
    ) {
        try {
            List<Order> orders = orderService.getUserOrders(userId, status);
            
            List<OrderResponseDTO> responses = orders.stream()
                    .map(order -> OrderResponseDTO.builder()
                            .orderId(order.getOrderId())
                            .orderStatus(order.getOrderStatus())
                            .orderDate(order.getOrderDate())
                            .updatedAt(order.getOrderUpdatedAt())
                            .totalAmount(order.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
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
    
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id, @RequestParam Long userId) {
        try {
            Order order = orderService.getOrderWithDetails(id, userId);
            // Chuyển đổi thành DTO phù hợp với frontend
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getOrderId());
            response.put("status", order.getOrderStatus());
            response.put("orderDate", order.getOrderDate());
            response.put("updatedAt", order.getOrderUpdatedAt());
            response.put("totalAmount", order.getTotalAmount());
            response.put("itemsTotal", order.getItemsTotal());
            response.put("deliveryFee", order.getDeliveryFee());
            response.put("note", order.getNote() != null ? order.getNote() : "");
            response.put("restaurantId", order.getRestaurantId());
            response.put("shippingAddress", order.getShippingAddress().getAdr());
            response.put("items", order.getOrderItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("name", item.getName());
                itemMap.put("price", item.getPrice());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("imageUrl", item.getImageUrl());
                itemMap.put("note", item.getNote() != null ? item.getNote() : "");
                return itemMap;
            }).collect(Collectors.toList())
        );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @RequestParam Long userId) {
        try {
            Order cancelledOrder = orderService.cancelOrder(id, userId);
            
            OrderResponseDTO response = OrderResponseDTO.builder()
                    .orderId(cancelledOrder.getOrderId())
                    .orderStatus(cancelledOrder.getOrderStatus())
                    .orderDate(cancelledOrder.getOrderDate())
                    .updatedAt(cancelledOrder.getOrderUpdatedAt())
                    .totalAmount(cancelledOrder.getTotalAmount())
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi hủy đơn hàng: " + e.getMessage());
        }
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

    @PostMapping("/draft")
    public ResponseEntity<?> createDraftOrder(@RequestParam Long userId) {
        try {
            Order draftOrder = orderService.createDraftOrder(userId);
            
            OrderResponseDTO response = OrderResponseDTO.builder()
                    .orderId(draftOrder.getOrderId())
                    .orderStatus(draftOrder.getOrderStatus())
                    .orderDate(draftOrder.getOrderDate())
                    .totalAmount(draftOrder.getItemsTotal())
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi tạo đơn hàng nháp: " + e.getMessage());
        }
    }
}
