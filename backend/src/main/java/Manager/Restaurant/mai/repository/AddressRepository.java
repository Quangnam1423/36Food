package Manager.Restaurant.mai.repository;

import Manager.Restaurant.mai.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserUserIdAndIsDeletedFalse(Long userId);
}
