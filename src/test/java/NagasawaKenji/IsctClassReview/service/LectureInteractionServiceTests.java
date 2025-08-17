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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
// whenなどの共通処理の内、使わないものによるエラーの防止
@MockitoSettings(strictness = Strictness.LENIENT)
public class LectureInteractionServiceTests {

    @Mock LectureRepository lectureRepo;
    @Mock ReviewRepository reviewRepo;
    @Mock AttachmentRepository attachmentRepo;
    @Mock UserRepository userRepo;

    @TempDir Path tmpDir;

    LectureInteractionService service;

    private User user
            = new User(
                    "テストユーザー",
            "testPassword",
            "テストユーザー",
            LocalDateTime.now(),
            "test@m.titech.ac.jp"
            );


    @BeforeEach
    void setUp() {
        service = new LectureInteractionService(
                lectureRepo, reviewRepo, attachmentRepo, userRepo,
                tmpDir.toString()
        );
    }

    @Test
    @DisplayName("正常にファイルが渡された場合、保存される")
    void saveAttachment_success() throws Exception {

        CustomUserDetails principal = new CustomUserDetails(user);

        Lecture mockLecture = new Lecture();
        mockLecture.setId((short) 1);
        when(lectureRepo.findById((short) 1))
                .thenReturn(Optional.of(mockLecture));

        when(userRepo.findByUserName("テストユーザー"))
                .thenReturn(Optional.of(user));

        byte[] content = "これはテストファイルです".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "mockFile", "testfile.txt", "text/plain", content
        );

        AttachmentForm mockAttachmentForm = new AttachmentForm();
        mockAttachmentForm.setFile(mockFile);
        mockAttachmentForm.setLectureId((short) 1);
        mockAttachmentForm.setDescription("テスト用のファイルです");

        service.saveAttachment((short) 1, mockAttachmentForm, principal
                );

        ArgumentCaptor<Attachment> cap = ArgumentCaptor.forClass(Attachment.class);
        verify(attachmentRepo, times(1)).save(cap.capture());
        Attachment savedMockAttachment = cap.getValue();

        Path savedMockFilePath = Path.of(savedMockAttachment.getFilePath());
        assertTrue(Files.exists(savedMockFilePath), "ファイルが存在する");
        assertArrayEquals(content, Files.readAllBytes(savedMockFilePath), "ファイルが書き込まれていること");

        assertThat(savedMockAttachment.getLecture()).isSameAs(mockLecture);
        assertThat(savedMockAttachment.getUser()).isSameAs(user);
        assertThat(savedMockAttachment.getFileType()).isEqualTo("text/plain");
        assertThat(savedMockAttachment.getDescription()).isEqualTo("テスト用のファイルです");
    }

    @Test
    @DisplayName("lectureIdがDBに存在しない場合、NotFoundExceptionを投げる")
    void saveAttachment_notFound() {

        CustomUserDetails principal = new CustomUserDetails(user);

        when(lectureRepo.findById((short) 1)).thenReturn(Optional.empty());

        AttachmentForm mockAttachmentForm = new AttachmentForm();
        byte[] content = "これはテストファイルです".getBytes();
        mockAttachmentForm.setFile(new MockMultipartFile(
                "mockFile", "testfile.txt", "text/plain", content
        ));
        mockAttachmentForm.setDescription("");

        assertThrows(NotFoundException.class,
                () -> service.saveAttachment((short) 1, mockAttachmentForm, principal));
    }

    @Test
    @DisplayName("ファイルが空だった場合、BadRequestExceptionを投げる")
    void saveAttachment_BadRequest() {
        CustomUserDetails principal = new CustomUserDetails(user);
        Lecture mockLecture = new Lecture();
        mockLecture.setId((short) 1);
        when(lectureRepo.findById((short) 1))
                .thenReturn(Optional.of(mockLecture));

        User mockUser = new User();
        mockUser.setUserName("テストユーザー");
        when(userRepo.findByUserName("テストユーザー"))
                .thenReturn(Optional.of(mockUser));

        MockMultipartFile mockEmptyFile = new MockMultipartFile(
                "testFile", "testFile.txt", "text/plain", new byte[0]
        );

        AttachmentForm mockAttachmentForm = new AttachmentForm();
        mockAttachmentForm.setFile(mockEmptyFile);
        mockAttachmentForm.setLectureId((short) 1);
        mockAttachmentForm.setDescription("テスト用の空ファイルです");

        assertThrows(BadRequestException.class,
                () -> service.saveAttachment((short) 1, mockAttachmentForm, principal));

    }

    @Test
    @DisplayName("正常にreviewが渡された場合、保存する")
    void saveReview_success() {

        CustomUserDetails principal = new CustomUserDetails(user);
        Lecture mockLecture = new Lecture();
        mockLecture.setId((short) 1);
        when(lectureRepo.findById((short) 1))
                .thenReturn(Optional.of(mockLecture));

        User mockUser = new User();
        mockUser.setUserName("テストユーザー");
        when(userRepo.findByUserName("テストユーザー"))
                .thenReturn(Optional.of(mockUser));

        ReviewForm mockReviewForm = new ReviewForm();
        mockReviewForm.setLectureId((short) 1);
        mockReviewForm.setComment("テスト用のレビューです");
        mockReviewForm.setRating((short) 4);

        service.saveReview((short) 1, mockReviewForm, principal);

        ArgumentCaptor<Review> cap = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepo, times(1)).save(cap.capture());
        Review savedMockReview = cap.getValue();

        assertThat(savedMockReview.getLecture()).isSameAs(mockLecture);
        assertThat(savedMockReview.getUser()).isSameAs(mockUser);
        assertThat(savedMockReview.getRating()).isEqualTo((short) 4);
        assertThat(savedMockReview.getComment()).isEqualTo("テスト用のレビューです");
        assertNotNull(savedMockReview.getCreatedAt());
    }

    @Test
    @DisplayName("lectureIdがDBに存在しない場合、NotFoundExceptionを投げる")
    void saveReview_notFound_lecture() {
        CustomUserDetails principal = new CustomUserDetails(user);

        when(lectureRepo.findById((short) 1)).thenReturn(Optional.empty());

        ReviewForm mockReviewForm = new ReviewForm();
        mockReviewForm.setLectureId((short) 1);
        mockReviewForm.setComment("テスト用のレビューです");
        mockReviewForm.setRating((short) 4);

        assertThrows(NotFoundException.class,
                () -> service.saveReview((short) 1, mockReviewForm, principal));
    }

    @Test
    @DisplayName("mockUserがDBに存在しない場合、NotFoundExceptionを投げる")
    void saveReview_notFound_user() {

        CustomUserDetails principal = new CustomUserDetails(user);
        Lecture mockLecture = new Lecture();
        mockLecture.setId((short) 1);
        when(lectureRepo.findById((short) 1)).thenReturn(Optional.of(mockLecture));

        when(userRepo.findByUserName("テストユーザー")).thenReturn(Optional.empty());

        ReviewForm mockReviewForm = new ReviewForm();
        mockReviewForm.setLectureId((short) 1);
        mockReviewForm.setComment("テスト用のレビューです");
        mockReviewForm.setRating((short) 4);

        assertThrows(NotFoundException.class,
                () -> service.saveReview((short) 1, mockReviewForm, principal));
    }

    @Test
    @DisplayName("所有者がdeleteReviewを呼び出した場合、reviewが削除される")
    void deleteReview_success() {

        user.setId(1L);
        CustomUserDetails principal = new CustomUserDetails(user);

        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        when(reviewRepo.findById(1L))
                .thenReturn(Optional.of(review));


        when(userRepo.findByUserName(principal.getUsername()))
                .thenReturn(Optional.of(user));

        service.deleteReview(principal, 1L);

        verify(reviewRepo, times(1)).findById(1L);
        verify(reviewRepo, times(1)).delete(review);
        verifyNoMoreInteractions(reviewRepo);

    }

    @Test
    @DisplayName("レビューが存在しない場合、NotFoundExceptionを返す")
    void deleteReview_notFound() {
        CustomUserDetails principal = new CustomUserDetails(user);

        when(reviewRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.deleteReview(principal, 1L));
        verify(reviewRepo, never()).delete(any());
    }

    @Test
    @DisplayName("所有者でなければ、FORBIDDENを返す")
    void deleteReview_forbidden() {

        user.setId(1L);
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);

        User otherUser = new User("テストユーザー2", "testPassword2", "テストユーザー2",
                LocalDateTime.now(), "test2@m.titech.ac.jp");
        otherUser.setId(2L);
        CustomUserDetails principal = new CustomUserDetails(otherUser);

        when(reviewRepo.findById(1L))
                .thenReturn(Optional.of(review));

        when(userRepo.findByUserName(principal.getUsername()))
                .thenReturn(Optional.of(otherUser));

        assertThrows(ResponseStatusException.class, () -> service.deleteReview(principal, 1L));
        verify(reviewRepo, never()).delete(review);
    }

    @Test
    @DisplayName("所有者がdeleteAttachmentを呼び出した場合、attachmentが削除される")
    void deleteAttachment_success() throws Exception{

        user.setId(1L);
        CustomUserDetails principal = new CustomUserDetails(user);

        Path file = Files.createFile(tmpDir.resolve("test-to-delete.txt"));
        assertTrue(Files.exists(file));

        Attachment attachment = new Attachment();
        attachment.setFilePath(file.toString());
        attachment.setId(10L);
        attachment.setUser(user);
        when(attachmentRepo.findById(10L))
                .thenReturn(Optional.of(attachment));

        when(userRepo.findByUserName(principal.getUsername()))
                .thenReturn(Optional.of(user));

        service.deleteAttachment(principal, 10L);

        assertFalse(Files.exists(file));

        verify(attachmentRepo, times(1)).findById(10L);
        verify(attachmentRepo, times(1)).delete(attachment);
        verifyNoMoreInteractions(attachmentRepo);
    }

    @Test
    @DisplayName("アタッチメントが存在しない場合、NotFoundExceptionを返す")
    void deleteAttachment_notFound() {
        CustomUserDetails principal = new CustomUserDetails(user);

        when(attachmentRepo.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.deleteAttachment(principal, 10L));

        verify(attachmentRepo, times(1)).findById(10L);
        verifyNoMoreInteractions(attachmentRepo);
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    @DisplayName("所有者でなければ、FORBIDDENを返す")
    void deleteAttachment_forbidden() throws Exception {
        user.setId(1L);
        Path file = Files.createFile(tmpDir.resolve("to-check-forbidden.txt"));
        assertTrue(Files.exists(file));
        Attachment attachment = new Attachment();
        attachment.setId(10L);
        attachment.setUser(user);

        User otherUser = new User("テストユーザー2", "testPassword2", "テストユーザー2",
                LocalDateTime.now(), "test2@m.titech.ac.jp");
        otherUser.setId(2L);
        CustomUserDetails principal = new CustomUserDetails(otherUser);

        when(attachmentRepo.findById(10L))
                .thenReturn(Optional.of(attachment));

        when(userRepo.findByUserName(principal.getUsername()))
                .thenReturn(Optional.of(otherUser));

        assertThrows(ResponseStatusException.class,
                () -> service.deleteAttachment(principal, 10L));

        verify(attachmentRepo, times(1)).findById(10L);
        verifyNoMoreInteractions(attachmentRepo);
        verify(userRepo, times(1)).findByUserName(principal.getUsername());
        verifyNoMoreInteractions(userRepo);

    }
}
