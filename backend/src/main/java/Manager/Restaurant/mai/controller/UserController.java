package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.dto.UserProfileDTO;
import Manager.Restaurant.mai.entity.User;
import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final AddressRepository addressRepo;
    private final ReviewRepository reviewRepo;
    private final NotificationRepository notificationRepo;

    private final GeocodingService geocodingService;

    @GetMapping("/get-address")
    public ResponseEntity<?> getAddressFromCoordinates(
        @RequestParam double lat,
        @RequestParam double lon
    ) {
        try {
            String address = geocodingService.getAddressFromCoordinates(lat, lon);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("address", address);
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể lấy địa chỉ từ tọa độ");
        }
    }

    // ✅ Lấy thông tin người dùng theo ID (chỉ khi chưa bị xoá)
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam Long userId) {
        Optional<User> userOpt = userRepo.findById(userId)
                .filter(u -> !u.isDeleted());

        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        UserProfileDTO dto = new UserProfileDTO(
                user.getUserId(),
                user.getUserSlug(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserPhone(),
                user.getUserGender(),
                user.getUserAvatar(),
                user.getUserDob(),
                user.getUserStatus(),
                user.getRole() != null ? user.getRole().getRoleName() : null
        );

        return ResponseEntity.ok(dto);
    }


    // ✅ Cập nhật thông tin người dùng (chỉ nếu chưa bị xoá)
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestParam Long userId, @RequestBody User updatedInfo) {
        Optional<User> userOpt = userRepo.findById(userId)
                .filter(u -> !Boolean.TRUE.equals(u.isDeleted()));

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        user.setUserName(updatedInfo.getUserName());
        user.setUserPhone(updatedInfo.getUserPhone());
        user.setUserGender(updatedInfo.getUserGender());
        user.setUserAvatar(updatedInfo.getUserAvatar());
        user.setUserDob(updatedInfo.getUserDob());

        userRepo.save(user);
        return ResponseEntity.ok("Cập nhật thông tin thành công.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam Long userId) {
        Optional<User> userOpt = userRepo.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        if (Boolean.TRUE.equals(user.isDeleted())) {
            return ResponseEntity.badRequest().body("Người dùng đã bị xoá trước đó.");
        }

        // Soft delete user
        user.setDeleted(true);
        userRepo.save(user);

        // Soft delete liên quan
        notificationRepo.findByUserUserIdAndIsDeletedFalse(userId).forEach(n -> {
            n.setDeleted(true);
            notificationRepo.save(n);
        });

        addressRepo.findByUserUserIdAndIsDeletedFalse(userId).forEach(a -> {
            a.setDeleted(true);
            addressRepo.save(a);
        });

        reviewRepo.findByUser_UserIdAndIsDeletedFalse(userId).forEach(r -> {
            r.setDeleted(true);
            reviewRepo.save(r);
        });

        orderRepo.findByUserUserIdAndIsDeletedFalse(userId).forEach(o -> {
            o.setDeleted(true);
            orderRepo.save(o);
        });

        return ResponseEntity.ok("Đã xoá người dùng và các thông tin liên quan (soft delete).");
    }



    // ✅ Lấy danh sách tất cả người dùng chưa bị xoá
    @GetMapping("/all")
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        List<User> activeUsers = userRepo.findAll()
                .stream()
                .filter(u -> !Boolean.TRUE.equals(u.isDeleted()))
                .toList();

        List<UserProfileDTO> result = activeUsers.stream().map(user -> new UserProfileDTO(
                user.getUserId(),
                user.getUserSlug(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserPhone(),
                user.getUserGender(),
                user.getUserAvatar(),
                user.getUserDob(),
                user.getUserStatus(),
                user.getRole() != null ? user.getRole().getRoleName() : null
        )).toList();

        return ResponseEntity.ok(result);
    }

}
