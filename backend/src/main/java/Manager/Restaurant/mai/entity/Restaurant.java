package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imageUrl;
    private Float rating;
    private Integer ratingCount;
    //private String address;
    private String priceRange;
    private String openingStatus; // e.g. "OPEN", "CLOSED", "SCHEDULED"
    private String businessHours; // e.g. "08:00-22:00" or null
    private String serviceType;   // e.g. "DINE_IN", "DELIVERY", "TAKEAWAY"
    private String phoneNumber;
    private Integer likes;
    private Integer reviewsCount;
    private Double distance;

    @ElementCollection
    private List<String> categories = new ArrayList<>();

    private Long createdAt = System.currentTimeMillis();

    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MenuItem> menuItems;
}
