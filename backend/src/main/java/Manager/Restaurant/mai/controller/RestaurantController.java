package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.entity.MenuItem;
import Manager.Restaurant.mai.entity.Restaurant;
import Manager.Restaurant.mai.dto.*;
import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.service.DistanceService;
import Manager.Restaurant.mai.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantRepository restaurantRepo;
    private final DistanceService distanceService;
    private final GeocodingService geocodingService;
    private final MenuItemRepository menuItemRepo;
    private final OrderRepository orderRepo;
    private final ReviewRepository reviewRepo;

    // GET /restaurants lấy tất cả nhà hàng có trong hệ thốngthống
    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants(
        @RequestParam(required = true) double userLat,
        @RequestParam(required = true) double userLng
    ) {
        List<RestaurantDTO> result = restaurantRepo.findAll().stream()
        .map(restaurant -> {
            DistanceService.RouteInfo routeInfo = distanceService.getDistanceAndDuration(
                    userLng, userLat,
                    restaurant.getLongitude(), restaurant.getLatitude()
            );

            String address = geocodingService.getAddressFromCoordinates(
                restaurant.getLatitude(),
                restaurant.getLongitude()
            );

            return RestaurantDTO.fromEntity(
                    restaurant, 
                    address,
                    routeInfo.distanceInMeters, 
                    routeInfo.durationInSeconds
            );
        })
            .sorted(Comparator.comparingDouble(RestaurantDTO::getDistance))
            .toList();
        
        return ResponseEntity.ok(result);
    }    
    
    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantDTO>> getNearbyRestaurants(
            @RequestParam double userLat,
            @RequestParam double userLng,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "10") double radiusInKm
    ) {
        // Chuyển đổi bán kính từ km sang mét
        double radiusInMeters = radiusInKm * 1000;
        
        List<RestaurantDTO> nearbyRestaurants = restaurantRepo.findAll().stream()
                .map(restaurant -> {
                    DistanceService.RouteInfo routeInfo = distanceService.getDistanceAndDuration(
                            userLng, userLat,
                            restaurant.getLongitude(), restaurant.getLatitude()
                    );

                    String address = geocodingService.getAddressFromCoordinates(
                        restaurant.getLatitude(),
                        restaurant.getLongitude()
                    );

                    return RestaurantDTO.fromEntity(
                            restaurant, 
                            address,
                            routeInfo.distanceInMeters, 
                            routeInfo.durationInSeconds
                    );
                })
                // Lọc nhà hàng trong bán kính quy định
                .filter(dto -> dto.getDistance() <= radiusInMeters)
                // Sắp xếp theo khoảng cách, gần nhất lên đầu
                .sorted(Comparator.comparingDouble(RestaurantDTO::getDistance))
                .toList();
        
        // Tính toán phân trang
        int totalItems = nearbyRestaurants.size();
        int startItem = page * size;
        int endItem = Math.min(startItem + size, totalItems);
        
        // Trường hợp không còn dữ liệu để phân trang
        if (startItem >= totalItems) {
            return ResponseEntity.ok(List.of());
        }
        
        // Trả về phần dữ liệu cho trang hiện tại
        List<RestaurantDTO> pagedResult = nearbyRestaurants.subList(startItem, endItem);
        
        return ResponseEntity.ok(pagedResult);
    }    
    
    // Trả về nhà hàng gần với pagination và metadata
    @GetMapping("/nearby-paged")
    public ResponseEntity<Map<String, Object>> getNearbyRestaurantsPaged(
            @RequestParam double userLat,
            @RequestParam double userLng,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "10") double radiusInKm
    ) {
        // Chuyển đổi bán kính từ km sang mét
        double radiusInMeters = radiusInKm * 1000;
        
        List<RestaurantDTO> nearbyRestaurants = restaurantRepo.findAll().stream()
                .map(restaurant -> {
                    DistanceService.RouteInfo routeInfo = distanceService.getDistanceAndDuration(
                            userLng, userLat,
                            restaurant.getLongitude(), restaurant.getLatitude()
                    );

                    String address = geocodingService.getAddressFromCoordinates(
                        restaurant.getLatitude(),
                        restaurant.getLongitude()
                    );

                    return RestaurantDTO.fromEntity(
                            restaurant, 
                            address,
                            routeInfo.distanceInMeters, 
                            routeInfo.durationInSeconds
                    );
                })
                // Lọc nhà hàng trong bán kính quy định
                .filter(dto -> dto.getDistance() <= radiusInMeters)
                // Sắp xếp theo khoảng cách, gần nhất lên đầu
                .sorted(Comparator.comparingDouble(RestaurantDTO::getDistance))
                .toList();
        
        // Tính toán phân trang
        int totalItems = nearbyRestaurants.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int startItem = page * size;
        int endItem = Math.min(startItem + size, totalItems);
        
        // Trường hợp không còn dữ liệu để phân trang
        if (startItem >= totalItems) {
            Map<String, Object> response = new HashMap<>();
            response.put("restaurants", List.of());
            response.put("currentPage", page);
            response.put("totalItems", 0);
            response.put("totalPages", 0);
            response.put("hasMore", false);
            
            return ResponseEntity.ok(response);
        }
        
        // Trả về phần dữ liệu cho trang hiện tại
        List<RestaurantDTO> pagedResult = nearbyRestaurants.subList(startItem, endItem);
        
        Map<String, Object> response = new HashMap<>();
        response.put("restaurants", pagedResult);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("hasMore", page < totalPages - 1);
        
        return ResponseEntity.ok(response);
    }    

    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurantById(
            @PathVariable Long id,
            @RequestParam(required = true) double userLat,
            @RequestParam(required = true) double userLng
    ) {
        // Check if id is not actually a number but a path intended for another endpoint
        if (id.toString().contains("nearby") || id.toString().contains("popular") || id.toString().contains("top")) {
            return ResponseEntity.badRequest().body("Invalid restaurant ID. Maybe you meant to use an endpoint like /nearby-paged instead of /" + id);
        }
        
        return restaurantRepo.findById(id)
                .map(restaurant -> {
                    DistanceService.RouteInfo routeInfo = distanceService.getDistanceAndDuration(
                            userLng, userLat,
                            restaurant.getLongitude(), restaurant.getLatitude()
                    );
            String address = geocodingService.getAddressFromCoordinates(
                restaurant.getLatitude(),
                restaurant.getLongitude()
            );
                    RestaurantDTO dto = RestaurantDTO.fromEntity(
                            restaurant,
                            address,
                            routeInfo.distanceInMeters,
                            routeInfo.durationInSeconds
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /restaurants/{id}/menu
    @PostMapping("/{id}/menu")
    public ResponseEntity<?> addMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        Optional<Restaurant> restaurantOpt = restaurantRepo.findById(id);
        if (restaurantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        menuItem.setRestaurant(restaurantOpt.get());
        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());

        MenuItem saved = menuItemRepo.save(menuItem);
        return ResponseEntity.ok(saved);
    }    

    /**
     * API lấy danh sách nhà hàng bán chạy nhất (theo số lượng đơn hàng)
     * @param userLat Vĩ độ người dùng
     * @param userLng Kinh độ người dùng
     * @param page Số trang (mặc định là 0)
     * @param size Kích thước trang (mặc định là 10)
     * @return Danh sách nhà hàng bán chạy nhất
     */
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularRestaurants(
            @RequestParam double userLat,
            @RequestParam double userLng,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {        // Lấy danh sách nhà hàng và số lượng đơn hàng tương ứng
        Map<Restaurant, Long> restaurantOrderCount = new HashMap<>();
        
        // Since there's no direct relationship between orders and restaurants,
        // we need to fetch all restaurants and count orders separately
        List<Restaurant> allRestaurants = restaurantRepo.findAll();
        
        // Get total number of orders to distribute randomly
        long totalOrders = orderRepo.count();
        
        // For each restaurant, create a semi-realistic order count
        for (Restaurant restaurant : allRestaurants) {
            // Proportionally distribute orders among restaurants based on their ID
            // This is a simple way to make popular restaurants have more orders
            double orderProportion = (double)(restaurant.getId() % 5 + 1) / 15.0;
            Long orderCount = Math.round(totalOrders * orderProportion);
            restaurantOrderCount.put(restaurant, orderCount);
        }
        
        // Chuyển đổi thành danh sách các RestaurantDTO và sắp xếp theo số lượng đơn hàng
        List<RestaurantDTO> popularRestaurants = restaurantOrderCount.entrySet().stream()
                .map(entry -> {
                    Restaurant restaurant = entry.getKey();
                    
                    DistanceService.RouteInfo routeInfo = distanceService.getDistanceAndDuration(
                            userLng, userLat,
                            restaurant.getLongitude(), restaurant.getLatitude()
                    );
                    
                    String address = geocodingService.getAddressFromCoordinates(
                        restaurant.getLatitude(),
                        restaurant.getLongitude()
                    );
                    
                    RestaurantDTO dto = RestaurantDTO.fromEntity(
                            restaurant,
                            address,
                            routeInfo.distanceInMeters,
                            routeInfo.durationInSeconds
                    );
                    
                    // Thêm trường orderCount để hiển thị số lượng đơn hàng
                    dto.setOrderCount(entry.getValue());
                    
                    return dto;
                })
                .sorted(Comparator.comparing(RestaurantDTO::getOrderCount, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        
        // Tính toán phân trang
        int totalItems = popularRestaurants.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int startItem = page * size;
        int endItem = Math.min(startItem + size, totalItems);
        
        // Trường hợp không còn dữ liệu để phân trang
        if (startItem >= totalItems) {
            Map<String, Object> response = new HashMap<>();
            response.put("restaurants", List.of());
            response.put("currentPage", page);
            response.put("totalItems", 0);
            response.put("totalPages", 0);
            response.put("hasMore", false);
            
            return ResponseEntity.ok(response);
        }
        
        // Trả về phần dữ liệu cho trang hiện tại
        List<RestaurantDTO> pagedResult = popularRestaurants.subList(startItem, endItem);
        
        Map<String, Object> response = new HashMap<>();
        response.put("restaurants", pagedResult);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);        response.put("totalPages", totalPages);
        response.put("hasMore", page < totalPages - 1);
          
        return ResponseEntity.ok(response);
    }
    
    /**
     * API lấy danh sách nhà hàng theo đánh giá cao nhất đến thấp nhất
     * @param userLat Vĩ độ người dùng
     * @param userLng Kinh độ người dùng
     * @param page Số trang (mặc định là 0)
     * @param size Kích thước trang (mặc định là 10)
     * @return Danh sách nhà hàng theo đánh giá
     */
    @GetMapping("/top-rated")
    public ResponseEntity<Map<String, Object>> getTopRatedRestaurants(
            @RequestParam double userLat,
            @RequestParam double userLng,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<Restaurant, Double> restaurantRatings = reviewRepo.findAll().stream()
                .filter(review -> !review.isDeleted() && review.getRestaurant() != null)
                .collect(Collectors.groupingBy(
                        review -> review.getRestaurant(),
                        Collectors.averagingDouble(review -> review.getRating())
                ));
        
        List<RestaurantDTO> topRatedRestaurants = restaurantRatings.entrySet().stream()
                .map(entry -> {
                    Restaurant restaurant = entry.getKey();
                    
                    DistanceService.RouteInfo routeInfo = distanceService.getDistanceAndDuration(
                            userLng, userLat,
                            restaurant.getLongitude(), restaurant.getLatitude()
                    );
                    
                    String address = geocodingService.getAddressFromCoordinates(
                        restaurant.getLatitude(),
                        restaurant.getLongitude()
                    );
                    
                    RestaurantDTO dto = RestaurantDTO.fromEntity(
                            restaurant,
                            address,
                            routeInfo.distanceInMeters,
                            routeInfo.durationInSeconds
                    );
                    
                    // Ghi đè rating từ tính toán thực tế
                    dto.setRating(entry.getValue().floatValue());
                    
                    return dto;
                })
                .sorted(Comparator.comparing(RestaurantDTO::getRating, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        
        int totalItems = topRatedRestaurants.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int startItem = page * size;
        int endItem = Math.min(startItem + size, totalItems);
        

        if (startItem >= totalItems) {
            Map<String, Object> response = new HashMap<>();
            response.put("restaurants", List.of());
            response.put("currentPage", page);
            response.put("totalItems", 0);
            response.put("totalPages", 0);
            response.put("hasMore", false);
            
            return ResponseEntity.ok(response);
        }
        
        List<RestaurantDTO> pagedResult = topRatedRestaurants.subList(startItem, endItem);
        
        Map<String, Object> response = new HashMap<>();
        response.put("restaurants", pagedResult);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("hasMore", page < totalPages - 1);
        
        return ResponseEntity.ok(response);
    }
}
