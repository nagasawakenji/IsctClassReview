package NagasawaKenji.IsctClassReview.service;

import NagasawaKenji.IsctClassReview.dto.AttachmentForm;
import NagasawaKenji.IsctClassReview.dto.ReviewForm;
import NagasawaKenji.IsctClassReview.entity.Attachment;
import NagasawaKenji.IsctClassReview.entity.Lecture;
import NagasawaKenji.IsctClassReview.entity.Review;
import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.exception.BadRequestException;
import NagasawaKenji.IsctClassReview.exception.NotFoundException;
import NagasawaKenji.IsctClassReview.repository.AttachmentRepository;
import NagasawaKenji.IsctClassReview.repository.LectureRepository;
import NagasawaKenji.IsctClassReview.repository.ReviewRepository;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LectureInteractionService {
    private final LectureRepository lectureRepo;
    private final ReviewRepository reviewRepo;
    private final AttachmentRepository attachmentRepo;
    private final UserRepository userRepo;
    private final Path uploadRoot;

    @Autowired
    public LectureInteractionService(LectureRepository lectureRepo,
                             ReviewRepository reviewRepo,
                             AttachmentRepository attachmentRepo,
                             UserRepository userRepo,
                             @Value("${app.upload.dir}") String uploadDir) {
        this.lectureRepo = lectureRepo;
        this.reviewRepo = reviewRepo;
        this.attachmentRepo = attachmentRepo;
        this.userRepo = userRepo;
        this.uploadRoot = Paths.get(uploadDir);
    }

    @Transactional
    public void saveAttachment(Short lectureId,
                               AttachmentForm attachmentForm,
                               org.springframework.security.core.userdetails.User principal) {
        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new NotFoundException("Lecture not found: " + lectureId));

        User user = userRepo.findByUserName(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found: " + principal.getUsername()));

        MultipartFile file = attachmentForm.getFile();
        if (file.isEmpty()) {
            throw new BadRequestException("ファイルが指定されていません");
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");

        Path dest = uploadRoot.resolve(filename);

        try {
            // ディレクトリ作成（存在しなければ）
            Files.createDirectories(uploadRoot);
            // ファイル保存
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("ファイルの保存に失敗しました");
        }

        Attachment attachment = new Attachment(
                lecture, user,
                dest.toString(),
                file.getContentType(),
                LocalDateTime.now(),
                attachmentForm.getDescription()
        );
        attachmentRepo.save(attachment);
    }

    @Transactional
    public void saveReview(Short lectureId,
                           ReviewForm reviewForm,
                           org.springframework.security.core.userdetails.User principal) {
        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new NotFoundException("Lecture not found: " + lectureId));

        User user = userRepo.findByUserName(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found: " + principal.getUsername()));

        reviewRepo.save(new Review(
                user,  lecture,
                reviewForm.getRating(),
                reviewForm.getComment(),
                LocalDateTime.now()
        ));
    }
}
