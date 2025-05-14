package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.entity.MenuItem;
import Manager.Restaurant.mai.entity.Restaurant;
import Manager.Restaurant.mai.dto.*;
import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.service.DistanceService;
import Manager.Restaurant.mai.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantRepository restaurantRepo;
    private final DistanceService distanceService;
    private final GeocodingService geocodingService;
    private final MenuItemRepository menuItemRepo;

    // GET /restaurants
    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants(
        @RequestParam(required = true) double userLat,
        @RequestParam(required = true) double userLng
    ) {
        // List<RestaurantDTO> result = restaurantRepo.findAll().stream()
        //         .map(RestaurantDTO::fromEntity)
        //         .toList();
        // return ResponseEntity.ok(result);

        List<RestaurantDTO> result = restaurantRepo.findAll().stream()
        .map(restaurant -> {
            DistanceService.RouteInfo routeInfo = distanceService.getDistanceAndDuration(
                    userLng, userLat,
                    restaurant.getLongitude(), restaurant.getLatitude()
            );

            String address = geocodingService.getAddressFromCoordinates(
                restaurant.getLatitude(),
                restaurant.getLongitude()
            );

            return RestaurantDTO.fromEntity(
                    restaurant, 
                    address,
                    routeInfo.distanceInMeters, 
                    routeInfo.durationInSeconds
            );
        })
            .sorted(Comparator.comparingDouble(RestaurantDTO::getDistance))
            .toList();
        
        return ResponseEntity.ok(result);
    }

    // // GET /restaurants/{id}
    // @GetMapping("/{id}")
    // public ResponseEntity<?> getRestaurantById(@PathVariable Long id) {
    //     // return restaurantRepo.findById(id)
    //     //         .map(res -> ResponseEntity.ok(RestaurantDTO.fromEntity(res)))
    //     //         .orElse(ResponseEntity.notFound().build());
    // }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurantById(
            @PathVariable Long id,
            @RequestParam(required = true) double userLat,
            @RequestParam(required = true) double userLng
    ) {
        return restaurantRepo.findById(id)
                .map(restaurant -> {
                    DistanceService.RouteInfo routeInfo = distanceService.getDistanceAndDuration(
                            userLng, userLat,
                            restaurant.getLongitude(), restaurant.getLatitude()
                    );
            String address = geocodingService.getAddressFromCoordinates(
                restaurant.getLatitude(),
                restaurant.getLongitude()
            );
                    RestaurantDTO dto = RestaurantDTO.fromEntity(
                            restaurant,
                            address,
                            routeInfo.distanceInMeters,
                            routeInfo.durationInSeconds
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /restaurants/{id}/menu
    @PostMapping("/{id}/menu")
    public ResponseEntity<?> addMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        Optional<Restaurant> restaurantOpt = restaurantRepo.findById(id);
        if (restaurantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        menuItem.setRestaurant(restaurantOpt.get());
        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());

        MenuItem saved = menuItemRepo.save(menuItem);
        return ResponseEntity.ok(saved);
    }

    // PUT /restaurants/{restaurantId}/menu/{menuId}
    @PutMapping("/{restaurantId}/menu/{menuId}")
    public ResponseEntity<?> updateMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long menuId,
            @RequestBody MenuItem updatedItem
    ) {
        Optional<Restaurant> restaurantOpt = restaurantRepo.findById(restaurantId);
        Optional<MenuItem> itemOpt = menuItemRepo.findById(menuId);

        if (restaurantOpt.isEmpty() || itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MenuItem item = itemOpt.get();
        if (!item.getRestaurant().getId().equals(restaurantId)) {
            return ResponseEntity.badRequest().body("MenuItem không thuộc nhà hàng này.");
        }

        item.setName(updatedItem.getName());
        item.setDescription(updatedItem.getDescription());
        item.setPrice(updatedItem.getPrice());
        item.setCategory(updatedItem.getCategory());
        item.setImageUrl(updatedItem.getImageUrl());
        item.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(menuItemRepo.save(item));
    }
}
