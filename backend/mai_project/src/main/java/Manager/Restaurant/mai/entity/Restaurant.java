package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // ID của nhà hàng

    private String name; // Tên nhà hàng

    private String email; // Địa chỉ email của nhà hàng

    private String password; // Mật khẩu của nhà hàng

    private String phone;   // Số điện thoại của nhà hàng

    private String imageUrl; // Hình ảnh đại diện của nhà hàng

    private LocationData locationData;

    private String status; // ACTIVE, INACTIVE, PENDING_APPROVAL

    private Boolean verify; // Đã xác minh hay chưa

    @Column(columnDefinition = "json")
    private String roles; // JSON chứa danh sách các vai trò (ví dụ: ["ROLE_RESTAURANT_OWNER"])

    private String priceRange; // Phân khúc giá (ví dụ: "$", "$$", "$$$")

    private Float rating;   // Điểm đánh giá trung bình của nhà hàng (1-5 stars)

    private Integer ratingCount; // Số lượng đánh giá

    private Integer likes; // Số lượng người thích nhà hàng

    private Integer reviewsCount; // Số lượng đánh giá

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Ngày tạo nhà hàng

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<FoodItem> foodItems; // Danh sách món ăn của nhà hàng

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Review> reviews; // Danh sách đánh giá của nhà hàng

    @Embedded
    private BusinessHours businessHours; // Giờ mở cửa của nhà hàng

    @Enumerated(EnumType.STRING)
    private OpeningStatus openingStatus;   // Trạng thái mở cửa của nhà hàng (OPEN, CLOSED, TEMPORARILY_CLOSED)

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType; // mỗi nhà hàng chỉ cho cung cấp một loại dịch vụ cụ thể thôi nhé.
}