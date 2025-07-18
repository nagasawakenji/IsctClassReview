package NagasawaKenji.IsctClassReview.repository;

import NagasawaKenji.IsctClassReview.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Short> {

    public Optional<Course> findByMajorAndName(Major major, String name);


}
