package Manager.Restaurant.mai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserProfileDTO {
    private Long userId;
    private String userSlug;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userGender;
    private String userAvatar;
    private LocalDateTime userDob;
    private String userStatus;
    private String roleName;
}
