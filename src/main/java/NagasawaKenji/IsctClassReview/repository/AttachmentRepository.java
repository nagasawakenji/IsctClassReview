package NagasawaKenji.IsctClassReview.repository;

import NagasawaKenji.IsctClassReview.entity.Attachment;
import NagasawaKenji.IsctClassReview.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Short> {

    @Query("SELECT COUNT(a) FROM Attachment a WHERE a.lecture.id = :id")
    long countAttachments(@Param("id") Short classId);

    List<Attachment> findByLectureId(Short lectureId);
}
