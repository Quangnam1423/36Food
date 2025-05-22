package Manager.Restaurant.mai.service;

import Manager.Restaurant.mai.entity.Restaurant;
import Manager.Restaurant.mai.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    
    /**
     * Lấy thông tin nhà hàng theo ID
     */
    public Restaurant getRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà hàng với ID: " + restaurantId));
    }
}
