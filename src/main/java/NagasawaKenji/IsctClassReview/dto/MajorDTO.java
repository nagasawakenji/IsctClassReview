package NagasawaKenji.IsctClassReview.dto;

import java.util.ArrayList;
import java.util.List;

public class MajorDTO {
    private final String name;
    private final String url;

    private final List<CourseDTO> courses = new ArrayList<>();

    public MajorDTO(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<CourseDTO> getCourses() {
        return courses;
    }

    public void addCourse(CourseDTO courseConstant) {
        courses.add(courseConstant);
    }

    @Override
    public String toString() {
        return name + " → " + url + (courses.isEmpty() ? "" : " " + courses);
    }


}
