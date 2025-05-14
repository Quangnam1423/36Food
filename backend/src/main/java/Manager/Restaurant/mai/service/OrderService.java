package Manager.Restaurant.mai.service;

import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.AddressRepository;
import Manager.Restaurant.mai.repository.OrderRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    public Order placeOrder(Long userId, Long addressId) {
        Cart cart = cartService.getCartByUser(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống.");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ."));

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderCreatedAt(LocalDateTime.now());
        order.setOrderStatus("PENDING");
        order.setTotalAmount(BigDecimal.valueOf(cart.getTotalPrice()));
        order.setShippingAddress(address);
        order.setDeleted(false);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        return savedOrder;
    }
}

