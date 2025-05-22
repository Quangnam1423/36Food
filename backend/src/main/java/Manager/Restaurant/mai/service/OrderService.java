package Manager.Restaurant.mai.service;

import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * Tạo đơn hàng mới từ một đơn hàng cũ
     * @param orderId ID của đơn hàng cũ
     * @param userId ID của người dùng đang thực hiện đặt lại
     * @param latitude Vĩ độ hiện tại của người dùng (nếu có)
     * @param longitude Kinh độ hiện tại của người dùng (nếu có)
     * @return Đơn hàng mới được tạo
     */
    @Transactional
    public Order reorderFromExistingOrder(Long orderId, Long userId, Double latitude, Double longitude) {
        // Lấy thông tin đơn hàng cũ
        Order oldOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng."));
        
        // Kiểm tra xem người dùng có quyền đặt lại đơn hàng này không
        if (!oldOrder.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền đặt lại đơn hàng này.");
        }
        
        // Lấy thông tin người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));
        
        // Lấy thông tin nhà hàng
        Restaurant restaurant = restaurantRepository.findById(Long.parseLong(oldOrder.getRestaurantId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà hàng."));
        
        // Xác định địa chỉ giao hàng
        Address shippingAddress;
        String customerAddressStr;
        
        if (latitude != null && longitude != null) {
            // Sử dụng vị trí hiện tại của người dùng
            String userAddress = geocodingService.getAddressFromCoordinates(latitude, longitude);
            
            // Tạo địa chỉ mới từ vị trí hiện tại
            shippingAddress = new Address();
            shippingAddress.setUser(user);
            shippingAddress.setAdr(userAddress);
            shippingAddress.setLatitude(latitude);
            shippingAddress.setLongitude(longitude);
            shippingAddress.setCreatedAt(LocalDateTime.now());
            shippingAddress.setUpdatedAt(LocalDateTime.now());
            addressRepository.save(shippingAddress);
            
            customerAddressStr = userAddress;
        } else {
            // Sử dụng địa chỉ từ đơn hàng cũ
            shippingAddress = oldOrder.getShippingAddress();
            customerAddressStr = oldOrder.getCustomerAddress();
        }
        
        // Tính phí giao hàng
        BigDecimal deliveryFee = distanceCalculationService.calculateDeliveryFee(
                restaurant.getLatitude(), restaurant.getLongitude(),
                shippingAddress.getLatitude(), shippingAddress.getLongitude()
        );
        
        // Lấy tổng giá trị các món hàng từ đơn hàng cũ
        BigDecimal itemsTotal = oldOrder.getItemsTotal();
        
        // Tính tổng đơn hàng mới = giá trị món hàng + phí giao hàng
        BigDecimal totalAmount = itemsTotal.add(deliveryFee);
        
        // Tạo đơn hàng mới
        Order newOrder = Order.builder()
                .user(user)
                .orderStatus("PENDING")
                .orderDate(LocalDateTime.now())
                .orderCreatedAt(LocalDateTime.now())
                .orderUpdatedAt(LocalDateTime.now())
                .shippingAddress(shippingAddress)
                .restaurantId(oldOrder.getRestaurantId())
                .restaurantAddress(oldOrder.getRestaurantAddress())
                .customerAddress(customerAddressStr)
                .itemsTotal(itemsTotal)
                .deliveryFee(deliveryFee)
                .totalAmount(totalAmount)
                .note(oldOrder.getNote())
                .isDeleted(false)
                .build();
        
        Order savedOrder = orderRepository.save(newOrder);
        
        // Sao chép các món hàng từ đơn hàng cũ
        List<OrderItem> newOrderItems = oldOrder.getOrderItems().stream()
                .map(oldItem -> OrderItem.builder()
                        .name(oldItem.getName())
                        .price(oldItem.getPrice())
                        .quantity(oldItem.getQuantity())
                        .imageUrl(oldItem.getImageUrl())
                        .note(oldItem.getNote())
                        .restaurantId(oldItem.getRestaurantId())
                        .order(savedOrder)
                        .build())
                .collect(Collectors.toList());
        
        newOrderItems.forEach(savedOrder::addOrderItem);
        return orderRepository.save(savedOrder);
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
    
    /**
     * Lấy danh sách đơn hàng theo trạng thái và khoảng thời gian
     * @param status Trạng thái đơn hàng (PENDING, PREPARING, DELIVERING, COMPLETED, CANCELLED)
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách đơn hàng thỏa mãn điều kiện
     */
    public List<Order> getOrdersByStatusAndDateRange(String status, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(6); // Mặc định lấy 6 tháng gần đây
        }
        
        if (endDate == null) {
            endDate = LocalDateTime.now(); // Mặc định đến thời điểm hiện tại
        }
        
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByOrderStatusAndOrderDateBetweenOrderByOrderDateDesc(
                status.toUpperCase(), startDate, endDate);
        } else {
            return orderRepository.findByOrderDateBetweenOrderByOrderDateDesc(startDate, endDate);
        }
    }
    
    /**
     * Lấy danh sách đơn hàng của người dùng theo trạng thái và khoảng thời gian
     * @param userId ID của người dùng
     * @param status Trạng thái đơn hàng (PENDING, PREPARING, DELIVERING, COMPLETED, CANCELLED)
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách đơn hàng thỏa mãn điều kiện
     */
    public List<Order> getUserOrdersByStatusAndDateRange(Long userId, String status, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(6); // Mặc định lấy 6 tháng gần đây
        }
        
        if (endDate == null) {
            endDate = LocalDateTime.now(); // Mặc định đến thời điểm hiện tại
        }
        
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByUser_UserIdAndOrderStatusAndOrderDateBetweenOrderByOrderDateDesc(
                userId, status.toUpperCase(), startDate, endDate);
        } else {
            return orderRepository.findByUser_UserIdAndOrderDateBetweenOrderByOrderDateDesc(
                userId, startDate, endDate);
        }
    }
    
    /**
     * Lấy danh sách đơn hàng của người dùng theo danh sách trạng thái
     * @param userId ID của người dùng
     * @param statuses Danh sách trạng thái đơn hàng cần lấy
     * @return Danh sách đơn hàng theo các trạng thái chỉ định
     */
    public List<Order> getUserOrdersByStatuses(Long userId, List<String> statuses) {
        return orderRepository.findByUser_UserIdAndOrderStatusInOrderByOrderDateDesc(userId, statuses);
    }
    
    /**
     * Lấy danh sách đơn hàng của người dùng theo một trạng thái cụ thể
     * @param userId ID của người dùng
     * @param status Trạng thái đơn hàng cần lấy
     * @return Danh sách đơn hàng theo trạng thái chỉ định
     */
    public List<Order> getUserOrdersByStatus(Long userId, String status) {
        return orderRepository.findByUser_UserIdAndOrderStatusOrderByOrderDateDesc(userId, status);
    }
    
    /**
     * Lấy lịch sử đơn hàng của người dùng với phân trang
     * @param userId ID của người dùng
     * @param statuses Danh sách trạng thái đơn hàng cần lấy
     * @param page Số trang
     * @param size Kích thước trang
     * @return Map chứa thông tin phân trang và danh sách đơn hàng
     */
    public Map<String, Object> getUserOrdersHistoryWithPagination(Long userId, List<String> statuses, int page, int size) {
        // Tạo đối tượng Pageable để phân trang
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        
        // Lấy danh sách đơn hàng có phân trang
        Page<Order> orderPage = orderRepository.findByUser_UserIdAndOrderStatusInOrderByOrderDateDesc(userId, statuses, pageable);
        
        // Chuyển đổi danh sách đơn hàng thành danh sách chi tiết đơn hàng
        List<Map<String, Object>> orderResponses = orderPage.getContent().stream()
                .map(this::convertToDetailedResponse)
                .collect(Collectors.toList());
        
        // Tạo response chứa thông tin phân trang và danh sách đơn hàng
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderResponses);
        response.put("currentPage", orderPage.getNumber());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());
        response.put("pageSize", orderPage.getSize());
        response.put("hasMore", orderPage.hasNext());
        
        return response;
    }
    
    /**
     * Lấy danh sách đơn hàng của người dùng không bao gồm các trạng thái nhất định
     * @param userId ID của người dùng
     * @param excludedStatuses Danh sách trạng thái đơn hàng cần loại trừ
     * @param page Số trang
     * @param size Kích thước trang
     * @return Map chứa thông tin phân trang và danh sách đơn hàng
     */
    public Map<String, Object> getUserOrdersExcludeStatusesWithPagination(Long userId, List<String> excludedStatuses, int page, int size) {
        // Tạo đối tượng Pageable để phân trang
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        
        // Lấy danh sách đơn hàng có phân trang
        Page<Order> orderPage = orderRepository.findByUser_UserIdAndOrderStatusNotInOrderByOrderDateDesc(userId, excludedStatuses, pageable);
        
        // Chuyển đổi danh sách đơn hàng thành danh sách chi tiết đơn hàng
        List<Map<String, Object>> orderResponses = orderPage.getContent().stream()
                .map(this::convertToDetailedResponse)
                .collect(Collectors.toList());
        
        // Tạo response chứa thông tin phân trang và danh sách đơn hàng
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderResponses);
        response.put("currentPage", orderPage.getNumber());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());
        response.put("pageSize", orderPage.getSize());
        response.put("hasMore", orderPage.hasNext());
        
        return response;
    }
    
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
}

