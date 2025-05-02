package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.entity.Role;
import Manager.Restaurant.mai.entity.User;
import Manager.Restaurant.mai.repository.RoleRepository;
import Manager.Restaurant.mai.repository.UserRepository;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("userEmail");

        if (email == null || payload.get("userPassword") == null || payload.get("userName") == null || payload.get("roleId") == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin bắt buộc!");
        }

        if (userRepo.findByUserEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng!");
        }

        Long roleId = Long.parseLong(payload.get("roleId").toString());
        Optional<Role> roleOpt = roleRepo.findById(roleId);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy role với ID: " + roleOpt.get().getRoleName());
        }

        User user = new User();
        user.setUserSlug((String) payload.get("userSlug"));
        user.setUserName((String) payload.get("userName"));
        user.setUserEmail(email);
        user.setUserPassword((String) payload.get("userPassword"));
        user.setUserSalt((String) payload.get("userSalt"));
        user.setUserGender((String) payload.get("userGender"));
        user.setUserPhone((String) payload.get("userPhone"));
        user.setUserAvatar((String) payload.get("userAvatar"));
        user.setUserStatus((String) payload.get("userStatus"));
        user.setRole(roleOpt.get());

        // Parse ngày sinh nếu có
        if (payload.get("userDob") != null) {
            try {
                user.setUserDob(LocalDateTime.parse((String) payload.get("userDob")));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Định dạng ngày sinh không hợp lệ!");
            }
        } else {
            user.setUserDob(LocalDateTime.now());
        }

        userRepo.save(user);
        return ResponseEntity.ok("Đăng ký thành công với role ID: " + roleOpt.get().getRoleName());
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepo.findByUserEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Email không tồn tại.");
        }

        User user = userOpt.get();
        if (!user.getUserPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Sai mật khẩu.");
        }

        return ResponseEntity.ok("Đăng nhập thành công! ");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginRequest {
        private String email;
        private String password;
    }
}
