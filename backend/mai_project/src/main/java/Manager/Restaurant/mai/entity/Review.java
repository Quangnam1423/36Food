package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private FoodItem food;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    private String content;

    private Float rating; // 1-5 stars

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    private Boolean isAnonymous = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;



    private String orderId; // Link to order if it's a verified purchase

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}