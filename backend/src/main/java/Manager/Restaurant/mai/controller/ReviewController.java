package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.dto.ReviewDTO;
import Manager.Restaurant.mai.dto.ReviewDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> data) {
        try {
            Long userId = Long.valueOf(data.get("userId").toString());
            Long restaurantId = Long.valueOf(data.get("restaurantId").toString());
            String content = data.get("content").toString();
            float rating = Float.parseFloat(data.get("rating").toString());
            boolean isAnonymous = Boolean.parseBoolean(data.get("isAnonymous").toString());
            List<String> imageUrls = (List<String>) data.getOrDefault("imageUrls", List.of());

            Long foodId = data.get("foodId") != null ? Long.valueOf(data.get("foodId").toString()) : null;
            Long orderId = data.get("orderId") != null ? Long.valueOf(data.get("orderId").toString()) : null;

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
                    .createdAt(Instant.now())
                    .isDeleted(false)
                    .build();

            reviewRepo.save(review);

            return ResponseEntity.ok(Map.of("message", "Đánh giá đã được thêm", "reviewId", review.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ hoặc thiếu.");
        }
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<?> getReviewsByRestaurant(@PathVariable Long restaurantId) {
        List<Review> reviews = reviewRepo.findByRestaurant_IdAndIsDeletedFalse(restaurantId);
        List<ReviewDTO> result = reviews.stream().map(ReviewDTO::fromEntity).toList();
        return ResponseEntity.ok(result);
    }    @GetMapping("/item/{itemId}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        return reviewRepo.findById(id).map(review -> {
            review.setDeleted(true);
            reviewRepo.save(review);
            return ResponseEntity.ok(Map.of("message", "Đánh giá đã được xoá mềm."));
        }).orElse(ResponseEntity.notFound().build());
    }
}
