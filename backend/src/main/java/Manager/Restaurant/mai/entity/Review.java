package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


import java.time.Instant;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "item_id")
    private MenuItem food;

    @ManyToOne @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private float rating; // 1.0 - 5.0

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean isAnonymous;

    @Column(nullable = false)
    private boolean isDeleted = false;
}
