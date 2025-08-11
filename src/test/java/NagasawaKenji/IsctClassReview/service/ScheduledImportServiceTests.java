package NagasawaKenji.IsctClassReview.service;

import NagasawaKenji.IsctClassReview.dto.ImportResult;
import NagasawaKenji.IsctClassReview.dto.MajorDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledImportServiceTests {

    private static final MajorDTO DUMMY_MAJOR = new MajorDTO("仮学院", "test/major");
    private static final List<MajorDTO> DUMMY_LIST  = List.of(DUMMY_MAJOR);

    @Mock DBRegisterService dbRegisterService;
    @Mock ExtractLectureService extractLectureService;

    @InjectMocks
    ScheduledImportService service;

    @Test
    @DisplayName("正常系: スクレイピング→インポートが正しく実行される")
    void scheduledImportAllLectures_success() throws Exception {
        when(extractLectureService.extractLectureInfo())
                .thenReturn(DUMMY_LIST);
        when(dbRegisterService.importAll(DUMMY_LIST))
                .thenReturn(new ImportResult("インポート完了", 1, 1, 1));

        service.scheduledImportAllLectures();

        verify(extractLectureService).extractLectureInfo();
        verify(dbRegisterService).importAll(DUMMY_LIST);
    }

    @Test
    @DisplayName("IOException時にはimportAllを呼び出さない")
    void scheduledImportAllLectures_IOException() throws Exception {
        when(extractLectureService.extractLectureInfo())
                .thenThrow(new IOException());

        service.scheduledImportAllLectures();

        verify(extractLectureService).extractLectureInfo();
        verify(dbRegisterService, never()).importAll(any());
    }

    @Test
    @DisplayName("DataIntegrityViolationException時にはimportAllが呼び出される")
    void scheduledImportAllLectures_DataIntegrityViolationException() throws Exception {
        when(extractLectureService.extractLectureInfo())
                .thenReturn(DUMMY_LIST);
        when(dbRegisterService.importAll(DUMMY_LIST))
                .thenThrow(new DataIntegrityViolationException("dup"));

        service.scheduledImportAllLectures();

        verify(extractLectureService).extractLectureInfo();
        verify(dbRegisterService).importAll(DUMMY_LIST);
    }

    @Test
    @DisplayName("その他エラー発生時はimportAllを呼ばない")
    void scheduledImportAllLectures_Exceptions() throws Exception {
        when(extractLectureService.extractLectureInfo())
                .thenThrow(new RuntimeException("boom"));

        service.scheduledImportAllLectures();

        verify(dbRegisterService, never()).importAll(any());
    }
}
