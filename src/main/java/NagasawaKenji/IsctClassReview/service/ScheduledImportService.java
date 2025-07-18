package NagasawaKenji.IsctClassReview.service;


import NagasawaKenji.IsctClassReview.dto.ImportResult;
import NagasawaKenji.IsctClassReview.dto.MajorDTO;
import NagasawaKenji.IsctClassReview.entity.Major;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ScheduledImportService {

    private final DBRegisterService dbRegisterService;
    private final ExtractLectureService extractLectureService;
    private static final Logger log = LoggerFactory.getLogger(ScheduledImportService.class);

    public ScheduledImportService(DBRegisterService dbRegisterService,
                                  ExtractLectureService extractLectureService) {
        this.dbRegisterService = dbRegisterService;
        this.extractLectureService = extractLectureService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void  scheduledImportAllLectures() {

        log.info("定期スクレイピングとインポートを開始します");

        try {
            List<MajorDTO> majorsTree = extractLectureService.extractLectureInfo();
            ImportResult result = dbRegisterService.importAll(majorsTree);
        } catch (IOException e) {
            log.error("定期スクレイピングに失敗しました");
        } catch (DataIntegrityViolationException e) {
            log.error("インポートに失敗しました");
        } catch (Exception e) {
            log.error("予期しないエラーによりインポートに失敗しました");
        }
    }




}
