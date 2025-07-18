package NagasawaKenji.IsctClassReview.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportResult {

    private final String message;
    private final int majorsCount;
    private final int coursesCount;
    private final int lecturesCount;


    @JsonCreator
    public ImportResult(
            @JsonProperty("message") String message,
            @JsonProperty("majorsCount") int majorsCount,
            @JsonProperty("coursesCount") int coursesCount,
            @JsonProperty("lecturesCount") int lecturesCount) {
        this.message = message;
        this.majorsCount = majorsCount;
        this.coursesCount = coursesCount;
        this.lecturesCount = lecturesCount;
    }

    public String getMessage() {
        return message;
    }

    public int getMajorsCount() {
        return majorsCount;
    }

    public int getCoursesCount() {
        return coursesCount;
    }

    public int getLecturesCount() {
        return lecturesCount;
    }
}
