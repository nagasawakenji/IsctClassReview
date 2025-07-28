package NagasawaKenji.IsctClassReview.service;


import NagasawaKenji.IsctClassReview.dto.CourseDTO;
import NagasawaKenji.IsctClassReview.dto.ImportResult;
import NagasawaKenji.IsctClassReview.dto.LectureDTO;
import NagasawaKenji.IsctClassReview.dto.MajorDTO;
import NagasawaKenji.IsctClassReview.entity.Course;
import NagasawaKenji.IsctClassReview.entity.Lecture;
import NagasawaKenji.IsctClassReview.entity.Major;
import NagasawaKenji.IsctClassReview.repository.CourseRepository;
import NagasawaKenji.IsctClassReview.repository.LectureRepository;
import NagasawaKenji.IsctClassReview.repository.MajorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DBRegisterServiceTests {

    @Mock MajorRepository majorRepo;
    @Mock CourseRepository courseRepo;
    @Mock LectureRepository lectureRepo;

    @InjectMocks
    DBRegisterService service;

    @Test
    @DisplayName("未登録のmajor,course,lectureが存在する場合は登録する")
    void importAll_success() {

        MajorDTO mockMajorDTO = new MajorDTO("仮学院", "/test/major");
        CourseDTO mockCourseDTO = new CourseDTO("仮学系", "/test/course");
        LectureDTO mockLectureDTO = new LectureDTO("仮講義", LocalDate.now().toString(), "/test/lecture", "test-000", "仮学系");
        mockCourseDTO.addLecture(mockLectureDTO);
        mockMajorDTO.addCourse(mockCourseDTO);

        List<MajorDTO> mockMajors = List.of(mockMajorDTO);

        // mockMajorDTOの情報がDBに存在しない時の挙動を書いている
        when(majorRepo.findByName("仮学院")).thenReturn(Optional.empty());
        Major mockSavedMajor = new Major("仮学院", "/test/major");
        when(majorRepo.save(any(Major.class))).thenReturn(mockSavedMajor);

        // mockCourseDTOの情報がDBに存在しない時の挙動を書いている
        when(courseRepo.findByMajorAndName(mockSavedMajor, "仮学系")).thenReturn(Optional.empty());
        Course mockSavedCourse = new Course("仮学系", "/test/course");
        when(courseRepo.save(any(Course.class))).thenReturn(mockSavedCourse);

        // mockLectureDTOの情報がDBに存在しない時の挙動を書いている
        when(lectureRepo.findByCourseAndCode(mockSavedCourse, "test-000")).thenReturn(Optional.empty());
        Lecture mockSavedLecture = new Lecture("test-000", "仮講義", mockLectureDTO.getOpeningPeriod(), "/test/lecture");
        when(lectureRepo.save(any(Lecture.class))).thenReturn(mockSavedLecture);

        ImportResult result = service.importAll(mockMajors);

        // importAllの返り値resultの値が想定と一致しているかの確認
        assertEquals(1, result.getMajorsCount());
        assertEquals(1, result.getCoursesCount());
        assertEquals(1, result.getLecturesCount());

        // saveがそれぞれ1回ずつ呼ばれたことの確認
        verify(majorRepo, times(1)).save(any(Major.class));
        verify(courseRepo, times(1)).save(any(Course.class));
        verify(lectureRepo, times(1)).save(any(Lecture.class));
    }

    @Test
    @DisplayName("すでに登録されているMajor,Course,Lectureに対しては登録を行わない")
    void importAll_alreadyExist() {

        MajorDTO mockMajorDTO = new MajorDTO("仮学院", "/test/major");
        CourseDTO mockCourseDTO = new CourseDTO("仮学系", "/test/course");
        LectureDTO mockLectureDTO = new LectureDTO("仮講義", LocalDate.now().toString(), "/test/lecture", "test-000", "仮学系");
        mockCourseDTO.addLecture(mockLectureDTO);
        mockMajorDTO.addCourse(mockCourseDTO);

        List<MajorDTO> mockMajors = List.of(mockMajorDTO);

        // mockMajorDTOの情報がすでにDBに存在する際の挙動を表している
        Major mockExistMajor = new Major("仮学院", "/test/major");
        when(majorRepo.findByName("仮学院")).thenReturn(Optional.of(mockExistMajor));

        // mockCourseDTOの情報がすでにDBに存在する際の挙動を表している
        Course mockExistCourse = new Course("仮学系", "/test/course");
        when(courseRepo.findByMajorAndName(mockExistMajor, "仮学系"))
                .thenReturn(Optional.of(mockExistCourse));

        // mockLectureDTOの情報がすでにDBに存在する際の挙動を表している
        Lecture mockExistLecture = new Lecture("test-000", "仮講義", mockLectureDTO.getOpeningPeriod(), "/test/lecture");
        when(lectureRepo.findByCourseAndCode(mockExistCourse, "test-000"))
                .thenReturn(Optional.of(mockExistLecture));

        ImportResult result = service.importAll(mockMajors);

        assertEquals(1, result.getMajorsCount());
        assertEquals(1, result.getCoursesCount());
        assertEquals(1, result.getLecturesCount());

        // 今回はsaveを呼び出していないので、0が期待される
        verify(majorRepo, times(0)).save(any(Major.class));
        verify(courseRepo, times(0)).save(any(Course.class));
        verify(lectureRepo, times(0)).save(any(Lecture.class));



    }
}
