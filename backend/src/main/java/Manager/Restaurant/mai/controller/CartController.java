package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.dto.CartItemRequest;
import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addToCart(@PathVariable Long userId, @RequestBody CartItemRequest request) {
        try {
            // Kiểm tra nếu nhà hàng khác thì reset giỏ hàng
            cartService.resetCartIfRestaurantDifferent(userId, request.getRestaurantId());
            
            // Thêm món vào giỏ hàng
            Cart cart = cartService.addItemToCart(userId, request);
            return ResponseEntity.ok(cart);
        } catch (IllegalStateException e) {
            // Trả về lỗi nếu không thể thêm món từ nhà hàng khác
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "requiresReset", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Không thể thêm món vào giỏ hàng: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Cart> clearCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    @DeleteMapping("/{userId}/remove/{itemId}")
    public ResponseEntity<Cart> removeItem(@PathVariable Long userId, @PathVariable String itemId) {
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }
    
    @PutMapping("/{userId}/update/{itemId}")
    public ResponseEntity<Cart> updateItemQuantity(
            @PathVariable Long userId, 
            @PathVariable String itemId,
            @RequestParam int quantity) {
        if (quantity <= 0) {
            return ResponseEntity.ok(cartService.removeItem(userId, itemId));
        }
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, quantity));
    }
    
    @PostMapping("/{userId}/confirm-reset")
    public ResponseEntity<Cart> confirmCartReset(@PathVariable Long userId, @RequestBody CartItemRequest request) {
        // Xóa giỏ hàng hiện tại
        cartService.clearCart(userId);
        
        // Thêm món mới vào giỏ hàng (đã trống)
        Cart cart = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(cart);
    }
    
    @GetMapping("/{userId}/ready")
    public ResponseEntity<?> isCartReadyForOrder(@PathVariable Long userId) {
        boolean isReady = cartService.isCartReadyForOrder(userId);
        return ResponseEntity.ok(Map.of("ready", isReady));
    }
}
