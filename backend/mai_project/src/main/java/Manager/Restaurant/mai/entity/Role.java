package Manager.Restaurant.mai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "role_slug", nullable = false)
    private String roleSlug;

    @Column(name = "role_status")
    private String roleStatus;

    @Column(name = "role_description", columnDefinition = "TEXT")
    private String roleDescription;

    @Column(name = "role_grants_actions")
    private String roleGrantsActions;

    @Column(name = "role_grants_attributes")
    private String roleGrantsAttributes;


    public Role(String roleName, String roleSlug, String roleStatus) {
        this.roleName = roleName;
        this.roleSlug = roleSlug;
        this.roleStatus = roleStatus;
    }

}
