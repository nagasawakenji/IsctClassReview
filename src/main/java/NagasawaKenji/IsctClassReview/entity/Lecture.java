package NagasawaKenji.IsctClassReview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "lectures")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String code;

    @NotNull
    @Size(max = 200)
    @Column(length = 200, nullable = false)
    private String title;

    @Size(max = 50)
    @Column(name = "opening_period", length = 50)
    private String openingPeriod;

    @Column(name="url", columnDefinition="TEXT")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Lecture(String code, String title,
                   String openingPeriod, String url) {
        this.code = code;
        this.title = title;
        this.openingPeriod = openingPeriod;
        this.url = url;
    }

    protected Lecture() {}

    public void setId(Short id) {
        this.id = id;
    }

    public Short getId() {
        return id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
