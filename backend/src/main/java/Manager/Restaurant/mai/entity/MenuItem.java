package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference
    private Restaurant restaurant;

    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    

    private boolean isAvailable = true; // Mặc định là có sẵn
    private Integer saleCount = 0; // Số lượng đã bán, mặc định là 0
    private Integer likes = 0; // Số lượt thích, mặc định là 0
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;    
    public MenuItem(Restaurant restaurant, 
            String name, 
            String description, 
            BigDecimal price, 
            String category, 
            String imageUrl, 
            LocalDateTime createdAt, 
            LocalDateTime updatedAt
        ) {
        this.restaurant = restaurant;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAvailable = true;
        this.saleCount = 0;
        this.likes = 0;
    }
    
    // Thêm các phương thức tiện ích
    public void incrementSaleCount(int quantity) {
        this.saleCount += quantity;
    }
    
    public void incrementLikes() {
        this.likes++;
    }
    
    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
    
    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}
