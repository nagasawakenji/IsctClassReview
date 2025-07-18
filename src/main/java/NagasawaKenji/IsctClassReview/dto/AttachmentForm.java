package NagasawaKenji.IsctClassReview.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class AttachmentForm {

    @NotNull
    private MultipartFile file;

    @NotNull
    private Short lectureId;

    @Size(max = 50)
    private String type;

    @Size(max = 500)
    private String description;

    public AttachmentForm(MultipartFile file, Short lectureId,
                          String type, String description) {
        this.file = file;
        this.lectureId = lectureId;
        this.type = type;
        this.description = description;
    }

    public  AttachmentForm() {};

    public MultipartFile getFile() {
        return file;
    }

    public Short getLectureId() {
        return lectureId;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setLectureId(Short lectureId) {
        this.lectureId = lectureId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
