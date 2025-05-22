package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.dto.ReviewDTO;
import Manager.Restaurant.mai.dto.ReviewDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final MenuItemRepository itemRepo;
    private final RestaurantRepository restaurantRepo;
    private final OrderRepository orderRepo;    
    
    @PostMapping
    public ResponseEntity<?> addReview(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestBody Map<String, Object> data) {
        try {
            // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
            Long userId = (Long) request.getAttribute("userId");
              if (userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Không tìm thấy thông tin người dùng trong token"
                ));
            }
            
            // Safely parse required fields
            Long restaurantId;
            String content;
            float rating;
            boolean isAnonymous;
            
            try {
                Object restaurantIdObj = data.get("restaurantId");
                if (restaurantIdObj == null || (restaurantIdObj instanceof String && ((String)restaurantIdObj).isEmpty())) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Thiếu thông tin nhà hàng"
                    ));
                }
                restaurantId = Long.valueOf(restaurantIdObj.toString());
                
                Object contentObj = data.get("content");
                content = contentObj != null ? contentObj.toString() : "";
                
                Object ratingObj = data.get("rating");
                if (ratingObj == null || (ratingObj instanceof String && ((String)ratingObj).isEmpty())) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Thiếu đánh giá sao"
                    ));
                }
                rating = Float.parseFloat(ratingObj.toString());
                
                Object isAnonymousObj = data.get("isAnonymous");
                isAnonymous = isAnonymousObj != null && Boolean.parseBoolean(isAnonymousObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Dữ liệu không hợp lệ: " + e.getMessage()
                ));
            }
            
            // Handle imageUrls safely - ensure it's never null
            List<String> imageUrls;
            try {
                Object imageUrlsObj = data.get("imageUrls");
                if (imageUrlsObj == null || (imageUrlsObj instanceof String && ((String)imageUrlsObj).isEmpty())) {
                    imageUrls = List.of(); // Empty list if imageUrls is null or empty string
                } else if (imageUrlsObj instanceof List) {
                    imageUrls = (List<String>) imageUrlsObj;
                } else {
                    imageUrls = List.of(); // Default to empty list for any other case
                }            } catch (Exception e) {
                imageUrls = List.of(); // Default to empty list if any exception occurs
            }

            // Safely parse optional fields
            Long foodId = null;
            Long orderId = null;
            
            try {
                Object foodIdObj = data.get("foodId");
                if (foodIdObj != null && !(foodIdObj instanceof String && ((String)foodIdObj).isEmpty())) {
                    foodId = Long.valueOf(foodIdObj.toString());
                }
                
                Object orderIdObj = data.get("orderId");
                if (orderIdObj != null && !(orderIdObj instanceof String && ((String)orderIdObj).isEmpty())) {
                    orderId = Long.valueOf(orderIdObj.toString());
                }
            } catch (NumberFormatException e) {
                // Just leave as null if there's a parsing error
            }

            User user = userRepo.findById(userId).orElseThrow();
            Restaurant restaurant = restaurantRepo.findById(restaurantId).orElseThrow();

            MenuItem food = (foodId != null) ? itemRepo.findById(foodId).orElse(null) : null;
            Order order = (orderId != null) ? orderRepo.findById(orderId).orElse(null) : null;

            Review review = Review.builder()
                    .user(user)
                    .restaurant(restaurant)
                    .food(food)
                    .order(order)
                    .content(content)                    
                    .rating(rating)
                    .imageUrls(imageUrls)
                    .isAnonymous(isAnonymous)
                    .createdAt(LocalDateTime.now())
                    .isDeleted(false)
                    .build();            Review savedReview = reviewRepo.save(review);

            // Chuyển đổi review thành DTO để trả về thông tin chi tiết hơn
            ReviewDTO reviewDTO = ReviewDTO.fromEntity(savedReview);
            
            return ResponseEntity.ok(Map.of(
                "message", "Đánh giá đã được thêm thành công", 
                "reviewId", savedReview.getId(),
                "review", reviewDTO
            ));
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi để debug
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Dữ liệu không hợp lệ hoặc thiếu: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<?> getReviewsByRestaurant(@PathVariable Long restaurantId) {
        List<Review> reviews = reviewRepo.findByRestaurant_IdAndIsDeletedFalse(restaurantId);
        List<ReviewDTO> result = reviews.stream().map(ReviewDTO::fromEntity).toList();
        return ResponseEntity.ok(result);
    }    
    
    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getReviewsByItem(@PathVariable Long itemId) {
        List<Review> reviews = reviewRepo.findByFood_ItemIdAndIsDeletedFalse(itemId);
        List<ReviewDTO> result = reviews.stream().map(ReviewDTO::fromEntity).toList();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Lấy danh sách đánh giá chi tiết (bao gồm thông tin người dùng) của một món ăn
     * @param itemId ID của món ăn
     * @return Danh sách các đánh giá chi tiết
     */
    @GetMapping("/item/{itemId}/details")
    public ResponseEntity<?> getDetailedReviewsByItem(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Kiểm tra xem món ăn có tồn tại không
        if (!itemRepo.existsById(itemId)) {
            return ResponseEntity.notFound().build();
        }
        
        List<Review> reviews = reviewRepo.findByFood_ItemIdAndIsDeletedFalse(itemId);
        
        // Sắp xếp theo thời gian mới nhất
        reviews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        // Tính toán phân trang
        int totalItems = reviews.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalItems);
        
        // Lấy danh sách review theo phân trang
        List<ReviewDetailDTO> pagedReviews;
        if (startIndex < totalItems) {
            pagedReviews = reviews.subList(startIndex, endIndex).stream()
                    .map(ReviewDetailDTO::fromEntity)
                    .toList();
        } else {
            pagedReviews = List.of();
        }
        
        // Tính toán rating trung bình
        float averageRating = 0;
        if (!reviews.isEmpty()) {
            averageRating = (float) reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0);
        }
        
        Map<String, Object> response = Map.of(
                "reviews", pagedReviews,
                "totalItems", totalItems,
                "totalPages", totalPages,
                "currentPage", page,
                "hasMore", page < totalPages - 1,
                "averageRating", averageRating
        );
        
        return ResponseEntity.ok(response);
    }    
    
    /**
     * Lấy tất cả danh sách đánh giá của người dùng (không phân trang)
     * @param userId ID của người dùng
     * @return Danh sách tất cả các đánh giá của người dùng
     */    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReviews(
            @PathVariable Long userId,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        System.out.println("Accessing /reviews/user/" + userId + " endpoint");
        System.out.println("Authorization header: " + request.getHeader("Authorization"));
        
        // Kiểm tra xem người dùng có tồn tại không
        if (!userRepo.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        List<Review> reviews = reviewRepo.findByUser_UserIdAndIsDeletedFalse(userId);
        
        // Sắp xếp theo thời gian mới nhất
        reviews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        // Chuyển đổi tất cả đánh giá sang DTO đơn giản hơn (chỉ chứa thông tin review, không kèm thông tin chi tiết người dùng)
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::fromEntity)
                .toList();
        
        // Tính toán rating trung bình cho tất cả đánh giá của người dùng
        float averageRating = 0;
        if (!reviews.isEmpty()) {
            averageRating = (float) reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0);
        }
        
        Map<String, Object> response = Map.of(
                "reviews", reviewDTOs,
                "totalCount", reviewDTOs.size(),
                "averageRating", averageRating
        );
        
        return ResponseEntity.ok(response);
    }/**
     * Lấy tất cả danh sách đánh giá của người dùng đăng nhập hiện tại (không phân trang)
     * @param request HttpServletRequest để lấy thông tin người dùng từ token JWT
     * @return Danh sách tất cả các đánh giá của người dùng hiện tại
     */    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserReviews(
            jakarta.servlet.http.HttpServletRequest request
    ) {
        // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
        Long userId = (Long) request.getAttribute("userId");
        
        System.out.println("Accessing /reviews/me endpoint with userId from token: " + userId);
        
        if (userId == null) {
            // Log headers for debugging
            System.out.println("Authorization header: " + request.getHeader("Authorization"));
            return ResponseEntity.status(401).body(Map.of(
                "error", "Không tìm thấy thông tin người dùng trong token"
            ));
        }
        
        List<Review> reviews = reviewRepo.findByUser_UserIdAndIsDeletedFalse(userId);
        
        // Sắp xếp theo thời gian mới nhất
        reviews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        // Chuyển đổi tất cả đánh giá sang DTO đơn giản hơn (chỉ chứa thông tin review, không kèm thông tin chi tiết người dùng)
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::fromEntity)
                .toList();
        
        // Tính toán rating trung bình cho tất cả đánh giá của người dùng
        float averageRating = 0;
        if (!reviews.isEmpty()) {
            averageRating = (float) reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0);
        }
        
        Map<String, Object> response = Map.of(
                "reviews", reviewDTOs,
                "totalCount", reviewDTOs.size(),
                "averageRating", averageRating
        );
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        return reviewRepo.findById(id).map(review -> {
            review.setDeleted(true);
            reviewRepo.save(review);
            return ResponseEntity.ok(Map.of("message", "Đánh giá đã được xoá mềm."));
        }).orElse(ResponseEntity.notFound().build());
    }
}
