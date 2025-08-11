package NagasawaKenji.IsctClassReview.repository;

import NagasawaKenji.IsctClassReview.entity.Lecture;
import NagasawaKenji.IsctClassReview.entity.Review;
import NagasawaKenji.IsctClassReview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT COUNT(r) FROM Review r WHERE r.lecture.id = :id")
    long countReviews(@Param("id") Short classId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.lecture.id = :id")
    Double avgRating(@Param("id") Short classId);

    @Query("""
                    SELECT r.lecture.id, AVG(r.rating)
                    FROM Review r
                    WHERE r.lecture IN :lectures
                    GROUP BY r.lecture.id
            """)
    List<Object[]> avgRatingByLectureRaw(@Param("lectures") List<Lecture> lectures);

    @Query("""
                    SELECT r.lecture.id, COUNT(r)
                    FROM Review r
                    WHERE r.lecture IN :lectures
                    GROUP BY r.lecture.id
            """)
    List<Object[]> countByLectureRaw(@Param("lectures") List<Lecture> lectures);

    List<Review> findByLectureId(Short lectureId);
    List<Review> findByUser(User user);
}