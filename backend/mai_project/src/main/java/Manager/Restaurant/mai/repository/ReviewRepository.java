package Manager.Restaurant.mai.repository;


import Manager.Restaurant.mai.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}

