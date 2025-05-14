package Manager.Restaurant.mai.dto;
import lombok.*;
import java.util.List;
import Manager.Restaurant.mai.entity.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Float rating;
    private Integer ratingCount;
    private String address;
    private String priceRange;
    private String openingStatus;
    private String businessHours;
    private String serviceType;
    private String phoneNumber;
    private Integer likes;
    private Integer reviewsCount;
    private Double distance;
    private List<String> categories;
    private Long createdAt;
    private Integer durationInMinutes;

    public static RestaurantDTO fromEntity(Restaurant res, String address, double distanceInMeters, double durationInSeconds) {
        return new RestaurantDTO(
                res.getId(),
                res.getName(),
                res.getImageUrl(),
                res.getRating(),
                res.getRatingCount(),
                address,
                res.getPriceRange(),
                res.getOpeningStatus(),
                res.getBusinessHours(),
                res.getServiceType(),
                res.getPhoneNumber(),
                res.getLikes(),
                res.getReviewsCount(),
                distanceInMeters / 1000.0,
                res.getCategories(),
                res.getCreatedAt(),
                (int)durationInSeconds / 60
        );
    }
}
