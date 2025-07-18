package NagasawaKenji.IsctClassReview.repository;

import NagasawaKenji.IsctClassReview.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Short> {

    public Optional<Lecture> findByCourseAndCode(Course course, String code);
}
