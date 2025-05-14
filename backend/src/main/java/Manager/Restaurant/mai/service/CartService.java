package Manager.Restaurant.mai.service;

import Manager.Restaurant.mai.dto.CartItemRequest;
import Manager.Restaurant.mai.entity.Cart;
import Manager.Restaurant.mai.entity.CartItem;
import Manager.Restaurant.mai.entity.User;
import Manager.Restaurant.mai.repository.CartItemRepository;
import Manager.Restaurant.mai.repository.CartRepository;
import Manager.Restaurant.mai.repository.UserRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public Cart getCartByUser(Long userId) {
        // Tìm giỏ hàng của user hoặc tạo mới nếu không có
        return cartRepository.findByUser_UserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
            Cart cart = Cart.builder()
                    .user(user)
                    .restaurantId(null)  // Giỏ hàng mới không có nhà hàng
                    .items(new ArrayList<>())
                    .build();
            return cartRepository.save(cart);
        });
    }

    public Cart addItemToCart(Long userId, CartItemRequest request) {
        // Tìm giỏ hàng của người dùng
        Cart cart = getCartByUser(userId);

        // Kiểm tra nếu giỏ hàng đã có nhà hàng khác, throw exception nếu có
        if (cart.getRestaurantId() != null && !cart.getRestaurantId().equals(request.getRestaurantId())) {
            throw new IllegalStateException("Chỉ được đặt món từ một nhà hàng tại một thời điểm.");
        }

        // Nếu giỏ hàng chưa có nhà hàng, set restaurantId mới
        if (cart.getRestaurantId() == null) {
            cart.setRestaurantId(request.getRestaurantId());
        }

        // Tạo món mới trong giỏ hàng
        CartItem item = CartItem.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .imageUrl(request.getImageUrl())
                .note(request.getNote())
                .cart(cart)
                .build();

        // Thêm món vào giỏ hàng
        cart.getItems().add(item);

        // Lưu giỏ hàng với các món mới
        return cartRepository.save(cart); // Cascade sẽ lưu CartItem
    }


    public Cart clearCart(Long userId) {
        Cart cart = getCartByUser(userId);
        cart.getItems().clear();
        cart.setRestaurantId(null);  // Reset restaurantId khi xóa tất cả món
        return cartRepository.save(cart);
    }

    public Cart removeItem(Long userId, String itemId) {
        Cart cart = getCartByUser(userId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        return cartRepository.save(cart);
    }
}
