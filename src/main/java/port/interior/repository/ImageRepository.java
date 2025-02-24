package port.interior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import port.interior.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
