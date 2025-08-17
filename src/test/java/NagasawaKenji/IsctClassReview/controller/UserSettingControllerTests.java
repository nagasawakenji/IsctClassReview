package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.repository.AttachmentRepository;
import NagasawaKenji.IsctClassReview.repository.ReviewRepository;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import NagasawaKenji.IsctClassReview.security.CustomUserDetails;
import NagasawaKenji.IsctClassReview.service.LectureInteractionService;
import NagasawaKenji.IsctClassReview.service.UserSettingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSettingController.class)
public class UserSettingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean UserRepository userRepo;
    @MockitoBean AttachmentRepository attachmentRepo;
    @MockitoBean ReviewRepository reviewRepo;
    @MockitoBean LectureInteractionService lectureInteractionService;
    @MockitoBean UserSettingService userSettingService;

    @Test
    @DisplayName("登録済みユーザーがshowSettingReviewFormを呼び出した場合、正しく各属性が付与される")
    void showSettingReviewForm_success() throws Exception {

        User testUser = new User("testUser", "testPassword", "testUser",
                LocalDateTime.now(), "test@m.titech.ac.jp");

        CustomUserDetails principal = new CustomUserDetails(testUser);



        when(userRepo.findByUserName(principal.getUsername()))
                .thenReturn(Optional.of(testUser));

        when(reviewRepo.findByUser(testUser)).thenReturn(List.of());

        mockMvc.perform(get("/setting/review").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("setting/review"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attribute("reviews", List.of()));
    }

    @Test
    @DisplayName("未登録のユーザーがshowSettingReviewFormを呼び出した場合、NotFoundExceptionを返す")
    void showSettingReviewForm_notFound() throws Exception {

        User testUser = new User("testUser", "testPassword", "testUser",
                LocalDateTime.now(), "test@m.titech.ac.jp");

        CustomUserDetails principal = new CustomUserDetails(testUser);

        when(userRepo.findByUserName(principal.getUsername()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/setting/review").with(user(principal)))
                .andExpect(status().isNotFound());
    }


}
