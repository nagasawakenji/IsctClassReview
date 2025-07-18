package NagasawaKenji.IsctClassReview.service;

import NagasawaKenji.IsctClassReview.dto.CourseDTO;
import NagasawaKenji.IsctClassReview.dto.ImportResult;
import NagasawaKenji.IsctClassReview.dto.LectureDTO;
import NagasawaKenji.IsctClassReview.dto.MajorDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExtractLectureService {

    private static final Logger log = LoggerFactory.getLogger(ExtractLectureService.class);

    public List<MajorDTO> extractLectureInfo() throws IOException {

        log.info("Webサイトのスクレイピングを開始します");

        Document doc = Jsoup.connect("https://syllabus.s.isct.ac.jp/")
                .timeout(10000)
                .get();

        log.info("学院、学系の情報の抽出を開始します");

        Element bachelorList = doc.selectFirst(
                "h3.c-h3:matchesOwn(学士課程) + ul.c-arrow-list-col.is-list-level1"
        );

        if (bachelorList == null) {
            System.out.println("学士課程セクションが見つかりませんでした");
            return Collections.emptyList();
        }

        List<MajorDTO> majors = new ArrayList<>();
        for (Element li1: bachelorList.select("li.is-list-item-level1")) {

            Element a1 = li1.selectFirst("> a");

            log.info("新規 Major の抽出: name='{}', url='{}'",
                    a1.text(), a1.absUrl("href"));

            MajorDTO major = new MajorDTO(a1.text(), a1.absUrl("href"));

            Elements subUl = li1.select("ul.c-arrow-list-col__child.is-list-level2");

            for (Element li2: subUl.select("li.is-list-item-level2")) {
                Element a2 = li2.selectFirst("> a");

                log.info("新規 Course の抽出: name='{}', url='{}'",
                        a2.text(), a2.absUrl("href"));

                CourseDTO sub = new CourseDTO(a2.text(), a2.absUrl("href"));
                major.addCourse(sub);
            }
            majors.add(major);
        }

        log.info("授業情報の抽出を開始します");

        for (MajorDTO major: majors) {
            for (CourseDTO course: major.getCourses()) {
                Document classesDoc = Jsoup.connect(course.getUrl())
                        .timeout(10000)
                        .get();

                Elements classes = classesDoc.select("div.c-tab__panel table.c-table.is-table-style1 tbody tr");

                for (Element eachLectureInfo: classes) {
                    Elements tds = eachLectureInfo.select("td");
                    if (tds.size() < 5) continue;
                    String code = tds.get(0).text().trim();
                    Element a = tds.get(1).selectFirst("a");
                    String url = a.absUrl("href");
                    String name = a.text().trim();

                    String courseName = tds.get(3).text().trim();

                    String openingPeriod = tds.get(4).text().trim();

                    log.info("新規 Lecture の抽出: name='{}', code='{}'",
                            name, code);

                    LectureDTO lec = new LectureDTO(name, openingPeriod, url, code, courseName);
                    course.addLecture(lec);

                }
            }
        }

        log.info("授業情報の抽出が完了しました");

        return majors;
    }


}

