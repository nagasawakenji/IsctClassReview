package NagasawaKenji.IsctClassReview.dto;

import java.util.ArrayList;
import java.util.List;

public class CourseDTO {

    private final String name;
    private final String url;

    private final List<LectureDTO> lectures = new ArrayList<>();

    public CourseDTO(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<LectureDTO> getLectures() {
        return lectures;
    }

    public void addLecture(LectureDTO lec) {
        lectures.add(lec);
    }
}
