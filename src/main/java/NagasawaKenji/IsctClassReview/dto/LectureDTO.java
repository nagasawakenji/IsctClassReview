package NagasawaKenji.IsctClassReview.dto;

public class LectureDTO {
    private final String name;
    private final String openingPeriod;
    private final String url;
    private final String code;
    private final String courseName;

    public LectureDTO(String name, String openingPeriod, String url,
                      String code, String courseName) {
        this.name = name;
        this.openingPeriod = openingPeriod;
        this.url = url;
        this.code = code;
        this.courseName = courseName;
    }

    public String getName() {
        return name;
    }

    public String getOpeningPeriod() {
        return openingPeriod;
    }

    public String getUrl() {
        return url;
    }

    public String getCode() {
        return code;
    }

    public String getCourse() {
        return courseName;
    }
}
