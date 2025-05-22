package Manager.Restaurant.mai.dto;

import Manager.Restaurant.mai.entity.Review;
import Manager.Restaurant.mai.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReviewDetailDTO {

    private Long id;
    private UserProfileDTO user;    private String content;
    private float rating;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private boolean isAnonymous;
    private Long foodId;
    private Long restaurantId;
    private Long orderId;

    public static ReviewDetailDTO fromEntity(Review review) {
        User user = review.getUser();
        UserProfileDTO userProfileDTO = new UserProfileDTO(
                user.getUserId(),
                user.getUserSlug(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserPhone(),
                user.getUserGender(),
                user.getUserAvatar(),
                user.getUserDob(),
                user.getUserStatus(),
                user.getRole().getRoleName()
        );

        // Nếu review là ẩn danh, chỉ giữ lại thông tin cần thiết và ẩn thông tin nhạy cảm
        if (review.isAnonymous()) {
            userProfileDTO.setUserEmail(null);
            userProfileDTO.setUserPhone(null);
            userProfileDTO.setUserSlug("anonymous");
            userProfileDTO.setUserName("Khách hàng ẩn danh");
        }

        return ReviewDetailDTO.builder()
                .id(review.getId())
                .user(userProfileDTO)
                .content(review.getContent())
                .rating(review.getRating())
                .imageUrls(review.getImageUrls())
                .createdAt(review.getCreatedAt())
                .isAnonymous(review.isAnonymous())
                .foodId(review.getFood() != null ? review.getFood().getItemId() : null)
                .restaurantId(review.getRestaurant().getId())
                .orderId(review.getOrder() != null ? review.getOrder().getOrderId() : null)
                .build();
    }
}
