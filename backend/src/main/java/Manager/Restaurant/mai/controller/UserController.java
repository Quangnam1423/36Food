package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.dto.ChangePasswordRequest;
import Manager.Restaurant.mai.dto.UserProfileDTO;
import Manager.Restaurant.mai.entity.User;
import Manager.Restaurant.mai.repository.*;
import Manager.Restaurant.mai.service.GeocodingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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

    // ✅ Lấy thông tin người dùng theo ID (chỉ khi chưa bị xoá) - Cách cũ với userId
    @GetMapping("/profile-by-id")
    public ResponseEntity<?> getProfileById(@RequestParam Long userId) {
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
    
    // ✅ Lấy thông tin người dùng từ token JWT của họ
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Không tìm thấy thông tin người dùng trong token");
        }
        
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

    
    // ✅ Cập nhật thông tin người dùng (chỉ nếu chưa bị xoá) - Cách cũ với userId
    @PutMapping("/update-by-id")
    public ResponseEntity<?> updateProfileById(@RequestParam Long userId, @RequestBody User updatedInfo) {
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
    
    // ✅ Cập nhật thông tin người dùng dựa trên JWT token
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @RequestBody User updatedInfo) {
        // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Không tìm thấy thông tin người dùng trong token");
        }
        
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
    
    // Xoá người dùng theo ID (cách cũ)
    @DeleteMapping("/delete-by-id")
    public ResponseEntity<?> deleteUserById(@RequestParam Long userId) {
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
    
    // Xoá người dùng hiện tại dựa trên JWT token
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        // Lấy userId từ token JWT (được thiết lập trong JwtAuthenticationFilter)
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Không tìm thấy thông tin người dùng trong token");
        }
        
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

   
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest changeRequest) {
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Không tìm thấy thông tin người dùng trong token");
        }
        
        
        if (changeRequest.getOldPassword() == null || changeRequest.getNewPassword() == null) {
            return ResponseEntity.status(400).body("Mật khẩu cũ và mật khẩu mới không được để trống");
        }
        
        Optional<User> userOpt = userRepo.findById(userId)
                .filter(u -> !Boolean.TRUE.equals(u.isDeleted()));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
        }

        User user = userOpt.get();
        
        
        if (!user.getUserPassword().equals(changeRequest.getOldPassword())) {
            return ResponseEntity.status(400).body("Mật khẩu cũ không chính xác");
        }
        
    
        user.setUserPassword(changeRequest.getNewPassword());
        
    
        userRepo.save(user);
        
        return ResponseEntity.ok("Đổi mật khẩu thành công");    
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
