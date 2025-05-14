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
    public ResponseEntity<Cart> addToCart(@PathVariable Long userId, @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, request));
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
}
