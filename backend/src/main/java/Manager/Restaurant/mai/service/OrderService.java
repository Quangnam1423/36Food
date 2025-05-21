package Manager.Restaurant.mai.service;

import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {    
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final DistanceCalculationService distanceCalculationService;
    private final GeocodingService geocodingService;

    /**
     * Creates a draft order from the cart
     * This saves the current cart state as a draft order without clearing the cart
     */
    @Transactional
    public Order createDraftOrder(Long userId) {
        Cart cart = cartService.getCartByUser(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống, không thể tạo đơn hàng nháp.");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));
        
        // Tạo đơn hàng với trạng thái DRAFT
        Order order = Order.builder()
                .user(user)
                .orderStatus("DRAFT")
                .restaurantId(cart.getRestaurantId())
                .orderDate(LocalDateTime.now())
                .orderCreatedAt(LocalDateTime.now())
                .orderUpdatedAt(LocalDateTime.now())
                .itemsTotal(BigDecimal.valueOf(cart.getTotalPrice()))
                .isDeleted(false)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Chuyển đổi CartItem thành OrderItem
        cart.getItems().forEach(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .name(cartItem.getName())
                    .price(BigDecimal.valueOf(cartItem.getPrice()))
                    .quantity(cartItem.getQuantity())
                    .imageUrl(cartItem.getImageUrl())
                    .note(cartItem.getNote())
                    .restaurantId(cart.getRestaurantId())
                    .order(savedOrder)
                    .build();
            
            savedOrder.addOrderItem(orderItem);
        });
        
        return orderRepository.save(savedOrder);
    }
    
    /**
     * Places an order from the current cart
     * @param userId User ID
     * @param addressId Shipping address ID
     * @param note Order note
     * @return The created Order
     */
    @Transactional
    public Order placeOrder(Long userId, Long addressId, String note) {
        Cart cart = cartService.getCartByUser(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống, không thể tạo đơn hàng.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ."));
        
        // Tìm thông tin nhà hàng để tính phí giao hàng
        Restaurant restaurant = restaurantRepository.findById(Long.parseLong(cart.getRestaurantId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà hàng."));
        
        // Tính phí giao hàng dựa trên khoảng cách
        BigDecimal deliveryFee = distanceCalculationService.calculateDeliveryFee(
                restaurant.getLatitude(), restaurant.getLongitude(),
                address.getLatitude(), address.getLongitude()
        );
        
        // Tính tổng giá trị các món hàng
        BigDecimal itemsTotal = BigDecimal.valueOf(cart.getTotalPrice());
        
        // Tổng đơn hàng = giá trị món hàng + phí giao hàng
        BigDecimal totalAmount = itemsTotal.add(deliveryFee);

        // Tạo đơn hàng mới với trạng thái PENDING
        Order order = Order.builder()
                .user(user)
                .orderStatus("PENDING")
                .orderDate(LocalDateTime.now())
                .orderCreatedAt(LocalDateTime.now())
                .orderUpdatedAt(LocalDateTime.now())
                .shippingAddress(address)
                .restaurantId(cart.getRestaurantId())
                .itemsTotal(itemsTotal)
                .deliveryFee(deliveryFee)
                .totalAmount(totalAmount)
                .note(note)
                .isDeleted(false)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Chuyển đổi CartItem thành OrderItem
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .name(cartItem.getName())
                        .price(BigDecimal.valueOf(cartItem.getPrice()))
                        .quantity(cartItem.getQuantity())
                        .imageUrl(cartItem.getImageUrl())
                        .note(cartItem.getNote())
                        .restaurantId(cart.getRestaurantId())
                        .order(savedOrder)
                        .build())
                .collect(Collectors.toList());
        
        orderItems.forEach(savedOrder::addOrderItem);
        orderRepository.save(savedOrder);
        
        // Xóa giỏ hàng sau khi đặt đơn thành công
        cartService.clearCart(userId);
        
        return savedOrder;
    }
      /**
     * Places an order directly from the cart using user's coordinates
     * @param userId User ID
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @return The created Order
     */    @Transactional
    public Order placeOrderFromCart(Long userId, Double latitude, Double longitude) {
        // Lấy thông tin giỏ hàng hiện tại
        Cart cart = cartService.getCartByUser(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống, không thể tạo đơn hàng.");
        }

        // Lấy thông tin người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));
                
        // Lấy thông tin nhà hàng để tính phí giao hàng
        Restaurant restaurant = restaurantRepository.findById(Long.parseLong(cart.getRestaurantId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà hàng."));
                
        // Sử dụng GeocodingService để chuyển đổi tọa độ người dùng thành địa chỉ
        String userAddress = geocodingService.getAddressFromCoordinates(latitude, longitude);
        
        // Lấy địa chỉ nhà hàng từ tọa độ
        String restaurantAddress = geocodingService.getAddressFromCoordinates(
                restaurant.getLatitude(), restaurant.getLongitude());
        
        // Tạo một đối tượng Address với tọa độ hiện tại của người dùng
        // Đối tượng này sẽ được lưu trong cơ sở dữ liệu và liên kết với đơn hàng
        Address orderAddress = new Address();
        orderAddress.setUser(user);
        orderAddress.setAdr(userAddress);  // Lưu địa chỉ được chuyển đổi từ tọa độ
        orderAddress.setLatitude(latitude);
        orderAddress.setLongitude(longitude);
        orderAddress.setCreatedAt(LocalDateTime.now());
        orderAddress.setUpdatedAt(LocalDateTime.now());
        addressRepository.save(orderAddress);
        
        // Tính phí giao hàng dựa trên khoảng cách thực tế
        BigDecimal deliveryFee = distanceCalculationService.calculateDeliveryFee(
                restaurant.getLatitude(), restaurant.getLongitude(),
                latitude, longitude
        );
        
        // Tính tổng giá trị các món hàng
        BigDecimal itemsTotal = BigDecimal.valueOf(cart.getTotalPrice());
        
        // Tổng đơn hàng = giá trị món hàng + phí giao hàng
        BigDecimal totalAmount = itemsTotal.add(deliveryFee);
        
        // Tạo đơn hàng mới với trạng thái PENDING và lưu địa chỉ dưới cả dạng chuỗi và đối tượng Address
        Order order = Order.builder()
                .user(user)
                .orderStatus("PENDING")
                .orderDate(LocalDateTime.now())
                .orderCreatedAt(LocalDateTime.now())
                .orderUpdatedAt(LocalDateTime.now())
                .shippingAddress(orderAddress) // Sử dụng địa chỉ thực tế của đơn hàng
                .restaurantId(cart.getRestaurantId())
                .restaurantAddress(restaurantAddress) // Địa chỉ nhà hàng (nơi giao) - thông tin chính
                .customerAddress(userAddress) // Địa chỉ người dùng (nơi nhận) - thông tin chính
                .itemsTotal(itemsTotal)
                .deliveryFee(deliveryFee)
                .totalAmount(totalAmount)
                .isDeleted(false)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Chuyển đổi CartItem thành OrderItem
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .name(cartItem.getName())
                        .price(BigDecimal.valueOf(cartItem.getPrice()))
                        .quantity(cartItem.getQuantity())
                        .imageUrl(cartItem.getImageUrl())
                        .note(cartItem.getNote())
                        .restaurantId(cart.getRestaurantId())
                        .order(savedOrder)
                        .build())
                .collect(Collectors.toList());
        
        orderItems.forEach(savedOrder::addOrderItem);
        orderRepository.save(savedOrder);
        
        // Xóa giỏ hàng sau khi đặt đơn thành công
        cartService.clearCart(userId);
        
        return savedOrder;
    }
    
    /**
     * Get orders for a user with pagination
     */
    public List<Order> getUserOrders(Long userId, String status) {
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByUser_UserIdAndOrderStatusOrderByOrderDateDesc(userId, status);
        } else {
            return orderRepository.findByUser_UserIdOrderByOrderDateDesc(userId);
        }
    }
    
    /**
     * Cancel an order
     */
    @Transactional
    public Order cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng."));
        
        if (!order.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền hủy đơn hàng này.");
        }
        
        if (order.getOrderStatus().equals("COMPLETED") || 
            order.getOrderStatus().equals("CANCELLED")) {
            throw new IllegalStateException("Không thể hủy đơn hàng đã hoàn thành hoặc đã hủy.");
        }
        
        if (order.getOrderStatus().equals("DELIVERING")) {
            throw new IllegalStateException("Không thể hủy đơn hàng đang giao.");
        }
        
        order.setOrderStatus("CANCELLED");
        order.setOrderUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
      /**
     * Get a specific order with details
     */
    public Order getOrderWithDetails(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng."));
        
        if (!order.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền xem đơn hàng này.");
        }
        
        return order;
    }
    
}

