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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {private final CartRepository cartRepository;
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
    
    public Map<String, Object> addItemToCart(Long userId, CartItemRequest request) {
        // Tìm giỏ hàng của người dùng
        Cart cart = getCartByUser(userId);

        // Flag để theo dõi nếu giỏ hàng đã được reset
        boolean wasReset = false;
        
        // Kiểm tra nếu giỏ hàng đã có nhà hàng khác, tự động reset giỏ hàng
        if (cart.getRestaurantId() != null && !cart.getRestaurantId().equals(request.getRestaurantId())) {
            // Xóa tất cả các mục trong giỏ hàng hiện tại
            cart.getItems().clear();
            wasReset = true;
        }        // Set restaurantId cho giỏ hàng
        cart.setRestaurantId(request.getRestaurantId());

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
        Cart savedCart = cartRepository.save(cart); // Cascade sẽ lưu CartItem
        
        // Trả về Map chứa thông tin cart và trạng thái reset
        Map<String, Object> result = new HashMap<>();
        result.put("cart", savedCart);
        result.put("wasReset", wasReset);
        
        return result;
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
        
        // Nếu xóa tất cả món thì reset restaurantId
        if (cart.getItems().isEmpty()) {
            cart.setRestaurantId(null);
        }
        
        return cartRepository.save(cart);
    }
      public Cart updateItemQuantity(Long userId, String itemId, int quantity) {
        if (quantity <= 0) {
            return removeItem(userId, itemId);
        }
        
        Cart cart = getCartByUser(userId);
        
        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(itemId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        
        return cartRepository.save(cart);
    }
    
    // Kiểm tra xem cart có sẵn sàng để đặt hàng không
    public boolean isCartReadyForOrder(Long userId) {
        Cart cart = getCartByUser(userId);
        return !cart.getItems().isEmpty() && cart.getRestaurantId() != null;
    }
}
