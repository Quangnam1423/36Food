package Manager.Restaurant.mai.dto;
import lombok.*;
import Manager.Restaurant.mai.entity.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicRestaurantDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Float rating;
    private Integer ratingCount;    
    private String address;
    private String priceRange;
    private String openingStatus;
    private String businessHours;
    private String phoneNumber;
    private Integer likes;
    private Integer reviewsCount;
    private Double distance;
    private Long createdAt;
    private Integer durationInMinutes;
    private Long orderCount; // Số lượng đơn hàng đã bán
    
    public static BasicRestaurantDTO fromEntity(Restaurant res, String address, double distanceInMeters, double durationInSeconds) {
        return new BasicRestaurantDTO(
                res.getId(),
                res.getName(),
                res.getImageUrl(),
                res.getRating(),
                res.getRatingCount(),
                address,
                res.getPriceRange(),
                res.getOpeningStatus(),
                res.getBusinessHours(),
                res.getPhoneNumber(),
                res.getLikes(),
                res.getReviewsCount(),
                distanceInMeters / 1000.0,
                res.getCreatedAt(),
                (int)durationInSeconds / 60,
                null // orderCount will be set separately when needed
        );
    }
    
    // Chuyển đổi từ RestaurantDTO (có thể được sử dụng trong phần chuyển đổi dần dần)
    public static BasicRestaurantDTO fromRestaurantDTO(RestaurantDTO dto) {
        return new BasicRestaurantDTO(
                dto.getId(),
                dto.getName(),
                dto.getImageUrl(),
                dto.getRating(),
                dto.getRatingCount(),
                dto.getAddress(),
                dto.getPriceRange(),
                dto.getOpeningStatus(),
                dto.getBusinessHours(),
                dto.getPhoneNumber(),
                dto.getLikes(),
                dto.getReviewsCount(),
                dto.getDistance(),
                dto.getCreatedAt(),
                dto.getDurationInMinutes(),
                dto.getOrderCount()
        );
    }
}
