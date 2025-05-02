package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.entity.Restaurant;
import Manager.Restaurant.mai.repository.MenuItemRepository;
import Manager.Restaurant.mai.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantRepository restaurantRepo;
    private final MenuItemRepository menuItemRepo;

    // GET /restaurants
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepo.findAll());
    }

    // GET /restaurants/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurantById(@PathVariable Long id) {
        Optional<Restaurant> restaurantOpt = restaurantRepo.findById(id);
        return restaurantOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /restaurants/{id}/menu
    @PostMapping("/{id}/menu")
    public ResponseEntity<?> addMenuItem(@PathVariable Long id, @RequestBody FoodItem menuItem) {
        Optional<Restaurant> restaurantOpt = restaurantRepo.findById(id);
        if (restaurantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        menuItem.setRestaurant(restaurantOpt.get());
        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());

        FoodItem saved = menuItemRepo.save(menuItem);
        return ResponseEntity.ok(saved);
    }

    // PUT /restaurants/{id}/menu/{menuId}
    @PutMapping("/{id}/menu/{menuId}")
    public ResponseEntity<?> updateMenuItem(
            @PathVariable Long id,
            @PathVariable Long menuId,
            @RequestBody FoodItem updatedItem
    ) {
        Optional<FoodItem> itemOpt = menuItemRepo.findById(menuId);

        if (itemOpt.isEmpty() || !itemOpt.get().getRestaurant().getResId().equals(id)) {
            return ResponseEntity.notFound().build();
        }

        FoodItem item = itemOpt.get();
        item.setName(updatedItem.getName());
        item.setDescription(updatedItem.getDescription());
        item.setPrice(updatedItem.getPrice());
        item.setCategory(updatedItem.getCategory());
        item.setImageUrl(updatedItem.getImageUrl());
        item.setUpdatedAt(LocalDateTime.now());

        menuItemRepo.save(item);
        return ResponseEntity.ok(item);
    }
}
