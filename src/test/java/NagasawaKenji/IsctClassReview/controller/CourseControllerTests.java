package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.config.GlobalExceptionHandler;
import NagasawaKenji.IsctClassReview.config.SecurityConfig;
import NagasawaKenji.IsctClassReview.entity.Course;
import NagasawaKenji.IsctClassReview.entity.Lecture;
import NagasawaKenji.IsctClassReview.repository.CourseRepository;
import NagasawaKenji.IsctClassReview.repository.ReviewRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(
        controllers = CourseController.class,
        // GlobalExceptionHandler をスキャン対象からはずす
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = GlobalExceptionHandler.class
        )
)
@WithMockUser
@Import(SecurityConfig.class)   // コントローラーで必要なセキュリティ設定だけインポート
public class CourseControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private CourseRepository courseRepo;
    @MockitoBean private ReviewRepository reviewRepo;

    @Test
    @DisplayName("DBに存在するcourseIdを受け取った場合、modelに各属性を正しく付与する")
    void showCourse_success() throws Exception {

        // 仮のコースを作成
        Course mockCourse = new Course();
        mockCourse.setId((short) 1);

        // 仮の講義を作成
        Lecture mockLecture = new Lecture();
        mockLecture.setId((short) 1);

        // 仮の講義を仮のコースの下の講義とした
        mockCourse.setLectures(List.of(mockLecture));

        when(courseRepo.findById((short) 1)).thenReturn(Optional.of(mockCourse));

        // thenReturn(List.of(new Object[]{mockLecture.getId(), 1L}))とするとエラーが起こる
        // 型推論が多分間違えているので、最初に明示する
        List<Object[]> mockCountList = new ArrayList<>();
        mockCountList.add(new Object[]{mockLecture.getId(), 1L});
        when(reviewRepo.countByLectureRaw(List.of(mockLecture)))
                .thenReturn(mockCountList);

        List<Object[]> mockAvgList = new ArrayList<>();
        mockAvgList.add(new Object[]{mockLecture.getId(), 1.0});
        when(reviewRepo.avgRatingByLectureRaw(List.of(mockLecture)))
                .thenReturn(mockAvgList);

        mockMvc.perform(get("/courses/{courseId}", (short) 1))
                .andExpect(status().isOk())
                .andExpect(view().name("course-detail"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attribute("course", mockCourse))
                .andExpect(model().attributeExists("lectures"))
                .andExpect(model().attribute("lectures", List.of(mockLecture)))
                .andExpect(model().attributeExists("reviewCountMap"))
                .andExpect(model().attribute("reviewCountMap", Map.of((short) 1, 1L)))
                .andExpect(model().attributeExists("avgRatingMap"))
                .andExpect(model().attribute("avgRatingMap", Map.of((short) 1, 1.0)));
    }

    @Test
    @DisplayName("DBに存在しないcourseIdを受け取った場合、エラーを返す")
    void showCourse_isNotFound() throws Exception {

        when(courseRepo.findById(ArgumentMatchers.any(Short.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/courses/{courseId}", (short) 99))
                .andExpect(status().isNotFound());


    }

}
