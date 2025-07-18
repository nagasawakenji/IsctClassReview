package NagasawaKenji.IsctClassReview.controller;


import NagasawaKenji.IsctClassReview.dto.ImportResult;
import NagasawaKenji.IsctClassReview.dto.MajorDTO;
import NagasawaKenji.IsctClassReview.repository.CourseRepository;
import NagasawaKenji.IsctClassReview.repository.LectureRepository;
import NagasawaKenji.IsctClassReview.repository.MajorRepository;
import NagasawaKenji.IsctClassReview.service.DBRegisterService;
import NagasawaKenji.IsctClassReview.service.ExtractLectureService;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class ApplicationRestController {

    private final CourseRepository courseRepo;
    private final LectureRepository lectureRepo;
    private final MajorRepository majorRepo;
    private final DBRegisterService dbRegisterService;
    private final ExtractLectureService extractLectureService;

    private static final Logger log = LoggerFactory.getLogger(ApplicationRestController.class);



    @Autowired
    public ApplicationRestController(CourseRepository courseRepo,
                                     LectureRepository lectureRepo,
                                     MajorRepository majorRepo,
                                     DBRegisterService dbRegisterService,
                                     ExtractLectureService extractLectureService) {
        this.courseRepo = courseRepo;
        this.lectureRepo = lectureRepo;
        this.majorRepo = majorRepo;
        this.dbRegisterService = dbRegisterService;
        this.extractLectureService = extractLectureService;
    }

    // 手動でデバックなどを行う際に使う、通常は自動で定期実行されるようになっている(予定)
    @PostMapping("/import/lectures")
    public ResponseEntity<ImportResult> importAllLecture() {

        try {
            List<MajorDTO> majorsTree = extractLectureService.extractLectureInfo();
            ImportResult result = dbRegisterService.importAll(majorsTree);

            log.info("正常に実行されました");

            return ResponseEntity.ok(result);
        } catch (IOException e) {

            log.error("IOExceptionが発生しました", e);

            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new ImportResult("外部サイト取得に失敗しました", 0, 0, 0));
        } catch (DataIntegrityViolationException e){

            log.error("DataIntegrityViolationExceptionが発生しました", e);

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ImportResult("データベース制約違反が発生しました", 0, 0, 0));
        } catch (Exception e) {

            log.error("予期しないエラーが発生しました", e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ImportResult("サーバーエラーが発生しました", 0, 0, 0));
        }



    }

    @GetMapping("/majors/refresh")
    public ResponseEntity<String> triggerMajorsRefresh() throws IOException {
        List<MajorDTO> majors = extractLectureService.extractLectureInfo();
        dbRegisterService.importAll(majors);

        return ResponseEntity.ok("Majors refreshed successfully");
    }




}
