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
import NagasawaKenji.IsctClassReview.security.CustomUserDetails;
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
import java.nio.file.*;
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
                               CustomUserDetails principal) {
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
                           CustomUserDetails principal) {
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

    @Transactional
    public void deleteAttachment(CustomUserDetails principal,
                                 Long attachmentId) {
        Attachment selectedAttachment = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException("Attachment not found" + attachmentId));

        User user = userRepo.findByUserName(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found: " + principal.getUsername()));
        User authUser = selectedAttachment.getUser();

        if (!user.getId().equals(authUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この操作を実行する権限がありません");
        }

        Path file = Paths.get(selectedAttachment.getFilePath());

        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("ファイルの削除に失敗しました: " + file.getFileName(), e);
        }

        attachmentRepo.delete(selectedAttachment);
    }

    @Transactional
    public void deleteReview(CustomUserDetails principal,
                             Long reviewId) {
        Review selectedReview = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("review not found: " + reviewId));

        User user = userRepo.findByUserName(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found: " + principal.getUsername()));
        User authUser =selectedReview.getUser();

        if (!user.getId().equals(authUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この操作を実行する権限がありません");
        }

        reviewRepo.delete(selectedReview);

    }
}
