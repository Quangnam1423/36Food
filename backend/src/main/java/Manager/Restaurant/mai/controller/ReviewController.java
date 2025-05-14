package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.dto.ReviewDTO;
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
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getReviewsByItem(@PathVariable Long itemId) {
        List<Review> reviews = reviewRepo.findByFood_ItemIdAndIsDeletedFalse(itemId);
        List<ReviewDTO> result = reviews.stream().map(ReviewDTO::fromEntity).toList();
        return ResponseEntity.ok(result);
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
