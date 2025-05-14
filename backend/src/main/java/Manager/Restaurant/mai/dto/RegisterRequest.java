package Manager.Restaurant.mai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String userSlug;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userSalt;
    private String userGender;
    private String userPhone;
    private String userAvatar;
    private String userStatus;
    private LocalDateTime userDob;
    private Long roleId;
}
