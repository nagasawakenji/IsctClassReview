package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.entity.Course;
import NagasawaKenji.IsctClassReview.entity.Lecture;
import NagasawaKenji.IsctClassReview.repository.CourseRepository;
import NagasawaKenji.IsctClassReview.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepo;
    private final ReviewRepository reviewRepo;

    public CourseController(CourseRepository courseRepo,
                            ReviewRepository reviewRepo) {
        this.courseRepo = courseRepo;
        this.reviewRepo = reviewRepo;
    }

    @GetMapping("/{courseId}")
    public String showCourse(Model model,
                             @PathVariable Short courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Lecture> lectures = course.getClasses();

        List<Object[]> rawCount = reviewRepo.countByLectureRaw(lectures);
        Map<Short, Long> reviewCountMap = rawCount.stream()
                .collect(Collectors.toMap(
                        row -> (Short) row[0],
                        row -> (Long) row[1]
                ));

        List<Object[]> rawAvgRating = reviewRepo.avgRatingByLectureRaw(lectures);
        Map<Short, Double> avgRatingMap = rawAvgRating.stream()
                .collect(Collectors.toMap(
                        row -> (Short) row[0],
                        row -> (Double) row[1]
                ));

        model.addAttribute("course", course);
        model.addAttribute("lectures", lectures);
        model.addAttribute("reviewCountMap", reviewCountMap);
        model.addAttribute("avgRatingMap", avgRatingMap);

        return "course-detail";




    }


}
