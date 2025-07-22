package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.config.GlobalExceptionHandler;
import NagasawaKenji.IsctClassReview.dto.AttachmentForm;
import NagasawaKenji.IsctClassReview.dto.ReviewForm;
import NagasawaKenji.IsctClassReview.entity.Attachment;
import NagasawaKenji.IsctClassReview.entity.Lecture;
import NagasawaKenji.IsctClassReview.entity.Review;
import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.repository.AttachmentRepository;
import NagasawaKenji.IsctClassReview.repository.LectureRepository;
import NagasawaKenji.IsctClassReview.repository.ReviewRepository;
import NagasawaKenji.IsctClassReview.service.LectureInteractionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(
        controllers = LectureController.class,
        // GlobalExceptionHandler をスキャン対象からはずす
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = GlobalExceptionHandler.class
        )
)
@WithMockUser
@Import(LectureController.class)
public class LectureControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private LectureRepository lectureRepo;
    @MockitoBean private ReviewRepository reviewRepo;
    @MockitoBean private AttachmentRepository attachmentRepo;
    @MockitoBean private LectureInteractionService lectureInteractionService;

    @Test
    @DisplayName("DBに登録されているlectureIdを受け取った場合、lecture-detailを返し、modelに正しい属性が付与されている")
    void showLecture_success() throws Exception {

        User mockUser = new User();
        mockUser.setId(1L);

        Lecture mockLecture = new Lecture();
        mockLecture.setId((short) 1);

        Review mockReview = new Review();
        mockReview.setId(1L);
        mockReview.setUser(mockUser);

        Attachment mockAttachment = new Attachment();
        mockAttachment.setId(1L);
        mockAttachment.setFilePath("/uploads/dummy.txt");
        mockAttachment.setCreatedAt(LocalDateTime.now());
        mockAttachment.setUser(mockUser);

        when(lectureRepo.findById((short) 1)).thenReturn(Optional.of(mockLecture));
        when(reviewRepo.findByLectureId((short) 1)).thenReturn(List.of(mockReview));
        when(attachmentRepo.findByLectureId((short) 1)).thenReturn(List.of(mockAttachment));

        mockMvc.perform(get("/lectures/{lectureId}", (short) 1))
                .andExpect(status().isOk())
                .andExpect(view().name("lecture-detail"))
                .andExpect(model().attributeExists("lecture"))
                .andExpect(model().attribute("lecture", mockLecture))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attribute("reviews", List.of(mockReview)))
                .andExpect(model().attributeExists("attachments"))
                .andExpect(model().attribute("attachments", List.of(mockAttachment)))
                .andExpect(model().attributeExists("reviewForm"))
                // 毎回新しくDTO を生成しているため、インスタンスの一致のみを検証している
                .andExpect(model().attribute("reviewForm", instanceOf(ReviewForm.class)))
                .andExpect(model().attributeExists("attachmentForm"))
                .andExpect(model().attribute("attachmentForm", instanceOf(AttachmentForm.class)));

    }

    @Test
    @DisplayName("DBに登録されていないlectureIdを受け取った場合、エラーを返す")
    void showLecture_isNotFound() throws Exception {

        when(lectureRepo.findById(ArgumentMatchers.any(short.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/lectures/{lectureId}", (short) 99))
                .andExpect(status().isNotFound());

    }
}
