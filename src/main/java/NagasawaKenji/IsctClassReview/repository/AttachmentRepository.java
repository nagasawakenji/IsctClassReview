package NagasawaKenji.IsctClassReview.repository;

import NagasawaKenji.IsctClassReview.entity.Attachment;
import NagasawaKenji.IsctClassReview.entity.Lecture;
import NagasawaKenji.IsctClassReview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    @Query("SELECT COUNT(a) FROM Attachment a WHERE a.lecture.id = :id")
    long countAttachments(@Param("id") Short classId);

    List<Attachment> findByLectureId(Short lectureId);
    List<Attachment> findByUser(User user);
}
