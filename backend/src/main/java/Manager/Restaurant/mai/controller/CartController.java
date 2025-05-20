package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.dto.CartItemRequest;
import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {    
    
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(HttpServletRequest request, @RequestBody CartItemRequest cartItemRequest) {
        try {
            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            // Thêm món vào giỏ hàng (sẽ tự động reset nếu là từ nhà hàng khác)
            Map<String, Object> result = cartService.addItemToCart(userId, cartItemRequest);
            
            // Lấy cart và trạng thái reset từ kết quả
            Cart cart = (Cart) result.get("cart");
            boolean wasReset = (boolean) result.get("wasReset");
            
            // Nếu giỏ hàng đã được reset, thông báo cho người dùng
            if (wasReset) {
                return ResponseEntity.ok(Map.of(
                    "cart", cart,
                    "message", "Giỏ hàng đã được làm mới vì bạn đã chọn món từ nhà hàng khác",
                    "wasReset", true
                ));
            }
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Không thể thêm món vào giỏ hàng: " + e.getMessage()
            ));
        }
    }    
    
    @GetMapping
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        // Lấy userId từ token JWT
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Không tìm thấy thông tin người dùng trong token"
            ));
        }
        
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(HttpServletRequest request) {
        // Lấy userId từ token JWT
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Không tìm thấy thông tin người dùng trong token"
            ));
        }
        
        return ResponseEntity.ok(cartService.clearCart(userId));
    }    
    
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> removeItem(HttpServletRequest request, @PathVariable String itemId) {
        // Lấy userId từ token JWT
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Không tìm thấy thông tin người dùng trong token"
            ));
        }
        
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }
      
    @PutMapping("/update/{itemId}")
    public ResponseEntity<?> updateItemQuantity(
            HttpServletRequest request,
            @PathVariable String itemId,
            @RequestParam int quantity) {
        // Lấy userId từ token JWT
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Không tìm thấy thông tin người dùng trong token"
            ));
        }
        
        if (quantity <= 0) {
            return ResponseEntity.ok(cartService.removeItem(userId, itemId));
        }
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, quantity));
    }
      @GetMapping("/ready")
    public ResponseEntity<?> isCartReadyForOrder(HttpServletRequest request) {
        // Lấy userId từ token JWT
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Không tìm thấy thông tin người dùng trong token"
            ));
        }
        
        boolean isReady = cartService.isCartReadyForOrder(userId);
        return ResponseEntity.ok(Map.of("ready", isReady));
    }
}
