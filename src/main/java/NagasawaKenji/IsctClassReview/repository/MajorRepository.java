package NagasawaKenji.IsctClassReview.repository;

import NagasawaKenji.IsctClassReview.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Short> {
    public Optional<Major> findByName(String name);
}
