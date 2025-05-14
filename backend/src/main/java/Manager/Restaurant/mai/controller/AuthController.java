package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.dto.LoginResponseDTO;
import Manager.Restaurant.mai.dto.RegisterRequest;
import Manager.Restaurant.mai.dto.RegisterResponseDTO;
import Manager.Restaurant.mai.dto.UserProfileDTO;
import Manager.Restaurant.mai.entity.Role;
import Manager.Restaurant.mai.entity.User;
import Manager.Restaurant.mai.repository.RoleRepository;
import Manager.Restaurant.mai.repository.UserRepository;
import Manager.Restaurant.mai.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final JwtService jwtService;   


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Validation cơ bản
        if (request.getUserEmail() == null || request.getUserPassword() == null || 
            request.getUserName() == null || request.getRoleId() == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin bắt buộc!");
        }

        if (userRepo.findByUserEmail(request.getUserEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng!");
        }

        Optional<Role> roleOpt = roleRepo.findById(request.getRoleId());
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy role với ID: " + request.getRoleId());
        }

        User user = new User();
        user.setUserSlug(request.getUserSlug());
        user.setUserName(request.getUserName());
        user.setUserEmail(request.getUserEmail());
        user.setUserPassword(request.getUserPassword());
        user.setUserSalt(request.getUserSalt());
        user.setUserGender(request.getUserGender());
        user.setUserPhone(request.getUserPhone());
        user.setUserAvatar(request.getUserAvatar());
        user.setUserStatus(request.getUserStatus());
        user.setRole(roleOpt.get());        // Xử lý ngày sinh
        if (request.getUserDob() != null) {
            user.setUserDob(request.getUserDob());
        } else {
            user.setUserDob(LocalDateTime.now());
        }

        // Lưu người dùng vào cơ sở dữ liệu
        User savedUser = userRepo.save(user);
        
        // Tạo JWT token cho người dùng mới đăng ký
        String token = jwtService.generateToken(
            savedUser.getUserEmail(),
            savedUser.getUserId(),
            savedUser.getRole() != null ? savedUser.getRole().getRoleName() : null
        );
        
        // Tạo response chứa thông báo và token
        RegisterResponseDTO response = new RegisterResponseDTO(
            "Đăng ký thành công với role: " + roleOpt.get().getRoleName(),
            token
        );
        
        return ResponseEntity.ok(response);
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                System.out.println("Login failed: Invalid login request");
                return ResponseEntity.status(400).body("Dữ liệu đăng nhập không hợp lệ");
            }
            
            System.out.println("Login attempt with email: " + loginRequest.getEmail());
            
            Optional<User> userOpt = userRepo.findByUserEmail(loginRequest.getEmail());

            if (userOpt.isEmpty()) {
                System.out.println("Login failed: Email not found: " + loginRequest.getEmail());
                return ResponseEntity.status(401).body("Email không tồn tại.");
            }

            User user = userOpt.get();
            if (!user.getUserPassword().equals(loginRequest.getPassword())) {
                System.out.println("Login failed: Incorrect password for email: " + loginRequest.getEmail());
                return ResponseEntity.status(401).body("Sai mật khẩu.");
            }            System.out.println("User found: " + user.getUserId() + ", " + user.getUserEmail() + ", role: " + 
                (user.getRole() != null ? user.getRole().getRoleName() : "null"));

            try {
                // Generate JWT token
                String token = jwtService.generateToken(
                    user.getUserEmail(),
                    user.getUserId(),
                    user.getRole() != null ? user.getRole().getRoleName() : null
                );
                
                if (token == null || token.isEmpty()) {
                    System.out.println("Login failed: Generated token is empty");
                    return ResponseEntity.status(500).body("Lỗi tạo token: Token trống");
                }
                
                System.out.println("Token generated successfully with length: " + token.length());
                // Log first few characters for debugging
                if (token.length() > 10) {
                    System.out.println("Token starts with: " + token.substring(0, 10) + "...");
                }
                
                // Validate token format - it should have 3 parts separated by dots
                String[] parts = token.split("\\.");
                if (parts.length != 3) {
                    System.out.println("Warning: Generated token does not appear to be a valid JWT format. Parts: " + parts.length);
                }

                // Create user profile
                UserProfileDTO userProfile = new UserProfileDTO(
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
                
                LoginResponseDTO response = new LoginResponseDTO(token, userProfile);                System.out.println("Login successful for: " + user.getUserEmail());
                
                // Return token and user info
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                System.out.println("Token generation error: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body("Lỗi tạo token: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    /**
     * Endpoint kiểm tra token và trả về thông tin người dùng hiện tại
     * @param request HttpServletRequest
     * @return Thông tin người dùng
     */    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // Lấy thông tin xác thực từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Không có người dùng đang đăng nhập");
        }        System.out.println("Authentication: " + authentication);
          // Lấy userId từ request attribute (được set trong JwtAuthenticationFilter)
        Long userId = (Long) request.getAttribute("userId");
          // Lấy thông tin người dùng từ database
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
        }
        
        User user = userOpt.get();
        UserProfileDTO userProfile = new UserProfileDTO(
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
        );        return ResponseEntity.ok(userProfile);
    }
    
      /**
     * Test endpoint to directly generate a JWT token
     */
    @GetMapping("/test-token")
    public ResponseEntity<?> testTokenGeneration() {
        try {
            String email = "test@example.com";
            Long userId = 1L;
            String role = "USER";
            
            System.out.println("Testing token generation with: " + email + ", " + userId + ", " + role);
            
            // Generate JWT token
            String token = jwtService.generateToken(email, userId, role);
            
            System.out.println("Generated test token: " + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "null"));
            
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(500).body("Token generation failed");
            }
            
            return ResponseEntity.ok(Map.of(
                "accessToken", token, 
                "tokenLength", token.length(),
                "firstTenChars", token.substring(0, Math.min(10, token.length()))
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error generating test token: " + e.getMessage());
        }
    }

      /**
     * Endpoint đăng nhập bằng access token từ header Authorization
     * @param request HttpServletRequest chứa header Authorization
     * @return Thông tin người dùng và token
     */
    @PostMapping("/token-login")
    public ResponseEntity<?> loginWithToken(HttpServletRequest request) {
        try {
            // Lấy token từ header Authorization
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(400).body("Token không được cung cấp hoặc không đúng định dạng. Sử dụng header: Authorization: Bearer [token]");
            }
            
            // Lấy token (bỏ phần "Bearer " ở đầu)
            String token = authHeader.substring(7);
            
            if (token.isEmpty()) {
                return ResponseEntity.status(400).body("Token không được để trống");
            }
            
            System.out.println("Token login attempt with token from Authorization header: " + 
                    token.substring(0, Math.min(10, token.length())) + "...");
            
            // Xác minh token
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(401).body("Token không hợp lệ hoặc đã hết hạn");
            }
            
            // Lấy thông tin từ token
            var claims = jwtService.extractAllClaims(token);
            String userEmail = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            
            // Tìm người dùng từ database
            Optional<User> userOpt = userRepo.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Không tìm thấy người dùng");
            }
            
            User user = userOpt.get();
            
            // Kiểm tra xem email trong token có khớp với người dùng không
            if (!user.getUserEmail().equals(userEmail)) {
                return ResponseEntity.status(401).body("Thông tin người dùng không khớp");
            }
            
            // Tạo user profile
            UserProfileDTO userProfile = new UserProfileDTO(
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
            
            // Tạo phản hồi với token hiện tại
            LoginResponseDTO response = new LoginResponseDTO(token, userProfile);
            System.out.println("Token login successful for: " + user.getUserEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("Token login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
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
