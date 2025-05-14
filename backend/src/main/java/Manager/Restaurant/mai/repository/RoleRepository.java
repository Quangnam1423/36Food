package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleSlug(String roleSlug);
}
