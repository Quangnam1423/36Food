package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.dto.OrderRequestDTO;
import Manager.Restaurant.mai.dto.OrderResponseDTO;
import Manager.Restaurant.mai.dto.PaymentDTO;
import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.service.CartService;
import Manager.Restaurant.mai.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    
    /**
     * Phương thức tiện ích để chuyển đổi từ Order sang OrderDetailResponse
     */
    private Map<String, Object> convertToDetailedResponse(Order order) {
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
        response.put("restaurantAddress", order.getRestaurantAddress()); // Địa chỉ nhà hàng
        response.put("customerAddress", order.getCustomerAddress()); // Địa chỉ khách hàng
        response.put("shippingAddress", order.getShippingAddress() != null ? order.getShippingAddress().getAdr() : null);
        
        // Chuyển đổi danh sách các món trong đơn hàng
        List<Map<String, Object>> items = new ArrayList<>();
        if (order.getOrderItems() != null) {
            items = order.getOrderItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("name", item.getName());
                itemMap.put("price", item.getPrice());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("imageUrl", item.getImageUrl());
                itemMap.put("note", item.getNote() != null ? item.getNote() : "");
                itemMap.put("subtotal", item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                return itemMap;
            }).collect(Collectors.toList());
        }
        response.put("items", items);
        
        return response;
    }

    
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(HttpServletRequest request, @RequestBody OrderRequestDTO dto) {
        try {
            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            if (dto.getLatitude() == null || dto.getLongitude() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Vui lòng cung cấp kinh độ và vĩ độ"
                ));
            }              // Gọi phương thức đặt hàng mới trong service
            Order order = orderService.placeOrderFromCart(userId, dto.getLatitude(), dto.getLongitude());
            
            // Trả về chi tiết đầy đủ của đơn hàng
            return ResponseEntity.ok(convertToDetailedResponse(order));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }    @GetMapping("/user")
    public ResponseEntity<?> getUserOrders(
            HttpServletRequest request,
            @RequestParam(required = false) String status
    ) {
        try {
            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            List<Order> orders = orderService.getUserOrders(userId, status);
            // Chuyển đổi danh sách đơn hàng thành danh sách chi tiết đơn hàng
            List<Map<String, Object>> responses = orders.stream()
                    .map(this::convertToDetailedResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
    }    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderStatus(@PathVariable Long id) {
        return orderRepo.findById(id)
                .filter(order -> !order.isDeleted())
                .map(order -> ResponseEntity.ok(convertToDetailedResponse(order)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
      @GetMapping("/{id}/details")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id, HttpServletRequest request) {
        try {
            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            Order order = orderService.getOrderWithDetails(id, userId);
            // Sử dụng phương thức chuyển đổi chung
            return ResponseEntity.ok(convertToDetailedResponse(order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
        }
    }    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        try {
            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            Order cancelledOrder = orderService.cancelOrder(id, userId);
            // Trả về chi tiết đầy đủ của đơn hàng đã hủy
            return ResponseEntity.ok(convertToDetailedResponse(cancelledOrder));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }    //Cập nhật trạng thái đơn hàng//
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

        Order updatedOrder = orderRepo.save(order);
        
        // Trả về chi tiết đầy đủ của đơn hàng sau khi cập nhật trạng thái
        return ResponseEntity.ok(convertToDetailedResponse(updatedOrder));
    }    @PostMapping("/draft")
    public ResponseEntity<?> createDraftOrder(HttpServletRequest request) {
        try {
            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            Order draftOrder = orderService.createDraftOrder(userId);
            // Trả về chi tiết đầy đủ của đơn hàng nháp
            return ResponseEntity.ok(convertToDetailedResponse(draftOrder));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi tạo đơn hàng nháp: " + e.getMessage());
        }
    }
}
