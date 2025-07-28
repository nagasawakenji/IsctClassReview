package NagasawaKenji.IsctClassReview.service;

import NagasawaKenji.IsctClassReview.dto.CourseDTO;
import NagasawaKenji.IsctClassReview.dto.LectureDTO;
import NagasawaKenji.IsctClassReview.dto.MajorDTO;
import org.hibernate.service.spi.InjectService;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExtractLectureServiceTests {

    @InjectMocks
    ExtractLectureService service;

    // 修正する予定
    @Test
    @DisplayName("htmlが正常な場合はmajorsを正しく抽出することができる")
    void extractLectureInfo_success() throws Exception {
        String homeHtml = Files.readString(Path.of("src/test/resources/home.html"));
        String lectureHtml = Files.readString(Path.of("src/test/resources/lecture.html"));
        Document homeDoc   = Jsoup.parse(homeHtml, "", Parser.htmlParser());
        Document lectureDoc = Jsoup.parse(lectureHtml, "", Parser.htmlParser());

        // 静的メソッドのmock化をしている
        try (MockedStatic<org.jsoup.Jsoup> jsoup = mockStatic(org.jsoup.Jsoup.class)) {
            Connection mockHomeConn = mock(Connection.class);
            jsoup.when(() -> Jsoup.connect("https://syllabus.s.isct.ac.jp/"))
                    .thenReturn(mockHomeConn);
            when(mockHomeConn.timeout(anyInt())).thenReturn(mockHomeConn);
            when(mockHomeConn.get()).thenReturn(homeDoc);

            Connection mockCourseConn = mock(Connection.class);
            jsoup.when(() -> Jsoup.connect("https://syllabus.s.isct.ac.jp/major1/courseA"))
                    .thenReturn(mockCourseConn);
            when(mockCourseConn.timeout(anyInt())).thenReturn(mockCourseConn);
            when(mockCourseConn.get()).thenReturn(lectureDoc);
            List<MajorDTO> majors = service.extractLectureInfo();

            assertThat(majors).hasSize(1);
            MajorDTO major = majors.get(0);
            assertThat(major.getName()).isEqualTo("学院1");

            List<CourseDTO> courses = major.getCourses();
            assertThat(courses).hasSize(1);
            CourseDTO course = courses.get(0);
            assertThat(course.getName()).isEqualTo("学系1");

            List<LectureDTO> lectures = course.getLectures();
            assertThat(lectures).hasSize(1);
            LectureDTO lecture = lectures.get(0);
            assertThat(lecture.getCode()).isEqualTo("TEST000");
            assertThat(lecture.getName()).isEqualTo("講義1");
            assertThat(lecture.getOpeningPeriod()).isEqualTo("2025年1月1日");
            assertThat(lecture.getUrl()).isEqualTo("https://test/lecture");
            assertThat(lecture.getCourse()).isEqualTo("学系1");

        }
    }

    static class JsoupMock {
        static ConnectionMockBuilder connect(String url) {
            throw new UnsupportedOperationException();
        }
        static class ConnectionMockBuilder {
            ConnectionMockBuilder(Document doc, int timeout) { /*…*/ }
            ConnectionMockBuilder timeout(int ms) { return this; }
            Document get() { return null; }
        }
    }
}
