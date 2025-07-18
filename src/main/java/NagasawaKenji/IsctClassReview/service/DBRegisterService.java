package NagasawaKenji.IsctClassReview.service;

import NagasawaKenji.IsctClassReview.dto.CourseDTO;
import NagasawaKenji.IsctClassReview.dto.ImportResult;
import NagasawaKenji.IsctClassReview.dto.MajorDTO;
import NagasawaKenji.IsctClassReview.dto.LectureDTO;
import NagasawaKenji.IsctClassReview.entity.Course;
import NagasawaKenji.IsctClassReview.entity.Lecture;
import NagasawaKenji.IsctClassReview.entity.Major;
import NagasawaKenji.IsctClassReview.repository.LectureRepository;
import NagasawaKenji.IsctClassReview.repository.CourseRepository;
import NagasawaKenji.IsctClassReview.repository.MajorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class DBRegisterService {

    private final LectureRepository lectureRepo;
    private final CourseRepository courseRepo;
    private final MajorRepository majorRepo;
    private static final Logger log = LoggerFactory.getLogger(DBRegisterService.class);

    @Autowired
    public DBRegisterService(LectureRepository lectureRepo,
                             CourseRepository courseRepo,
                             MajorRepository majorRepo) {
        this.lectureRepo = lectureRepo;
        this.courseRepo = courseRepo;
        this.majorRepo = majorRepo;
    }

    @Transactional
    public ImportResult importAll(List<MajorDTO> majorsTree) {

        log.info("majorsTreeのDBへの登録を開始します");

        for (MajorDTO majorCat: majorsTree) {

            log.debug("→ Major 処理開始: name='{}', url='{}'",
                    majorCat.getName(), majorCat.getUrl());

            Major major = majorRepo.findByName(majorCat.getName())
                    .orElseGet(() -> {

                            log.info("新規 Major を登録します: name='{}'",
                                    majorCat.getName());

                            return majorRepo.save(new Major(majorCat.getName(), majorCat.getUrl()));
                    });

            for (CourseDTO courseCat: majorCat.getCourses()) {

                log.debug("→ Course 処理開始: name='{}', url='{}'",
                        courseCat.getName(), courseCat.getUrl());

                Course course = courseRepo.findByMajorAndName(major, courseCat.getName())
                        .orElseGet(() -> {

                            log.info("新規 Course を登録します: name='{}'",
                                    courseCat.getName());

                            Course c = new Course(courseCat.getName(), courseCat.getUrl());
                            c.setMajor(major);
                            return courseRepo.save(c);
                        });

                for (LectureDTO courseClassCat: courseCat.getLectures()) {

                    log.trace("→ Lecture 処理開始: name='{}', code='{}'",
                            courseClassCat.getName(), courseClassCat.getCode());

                    lectureRepo.findByCourseAndCode(course, courseClassCat.getCode())
                            .orElseGet(() -> {

                                log.info("新規 Lecture を登録します: name='{}'",
                                        courseClassCat.getName());

                                Lecture lec = new Lecture(courseClassCat.getCode(), courseClassCat.getName(),
                                        courseClassCat.getOpeningPeriod(), courseClassCat.getUrl());
                                lec.setCourse(course);

                                return lectureRepo.save(lec);

                            });
                }
            }
        }

        int majorsCount = majorsTree.size();

        int coursesCount = majorsTree.stream()
                .mapToInt(m -> m.getCourses().size()).sum();

        int lecturesCount = majorsTree.stream()
                .flatMap(m -> m.getCourses().stream())
                .mapToInt(c -> c.getLectures().size()).sum();

        log.info("majorsTreeのDBへの登録が完了しました: majorsCount='{}', coursesCount='{}', lecturesCount='{}'",
                majorsCount,
                coursesCount,
                lecturesCount);

        ImportResult result = new ImportResult(
                "インポート完了",
                majorsCount,
                coursesCount,
                lecturesCount
        );

        return result;
    }
}
