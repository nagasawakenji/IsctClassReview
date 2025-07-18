package NagasawaKenji.IsctClassReview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewForm {

    @NotNull
    private Short lectureId;

    @NotNull
    @Max(5)
    @Min(1)
    private Short rating;

    @Size(max = 1000)
    private String comment;

    public ReviewForm(Short lectureId, Short rating,
                      String comment) {
        this.lectureId = lectureId;
        this.rating = rating;
        this.comment = comment;
    }

    public ReviewForm() {}

    public Short getLectureId() {
        return lectureId;
    }

    public void setLectureId(Short lectureId) {
        this.lectureId = lectureId;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
