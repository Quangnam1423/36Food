package Manager.Restaurant.mai.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private String name;
    private double price;
    private int quantity;
    private String imageUrl;
    private String note;
    private String restaurantId;
}
