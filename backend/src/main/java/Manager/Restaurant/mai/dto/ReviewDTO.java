package Manager.Restaurant.mai.dto;

import Manager.Restaurant.mai.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private Long userId;
    private String content;
    private float rating;
    private List<String> imageUrls;
    private Instant createdAt;
    private boolean isAnonymous;
    private Long foodId;
    private Long restaurantId;
    private Long orderId;

    public static ReviewDTO fromEntity(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getUser().getUserId(),
                review.getContent(),
                review.getRating(),
                review.getImageUrls(),
                review.getCreatedAt(),
                review.isAnonymous(),
                review.getFood() != null ? review.getFood().getItemId() : null,
                review.getRestaurant().getId(),
                review.getOrder() != null ? review.getOrder().getOrderId() : null
        );
    }
}
