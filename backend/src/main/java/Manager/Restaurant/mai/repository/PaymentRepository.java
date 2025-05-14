package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
