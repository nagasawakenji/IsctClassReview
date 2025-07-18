package NagasawaKenji.IsctClassReview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @NotNull
    @Size(max = 200)
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false,columnDefinition="TEXT")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> classes = new ArrayList<>();

    public Course(String name, String url) {
        this.name = name;
        this.url = url;
    }

    protected Course() {}

    public void setId(Short id) {
        this.id = id;
    }

    public Short getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public Major getMajor() {
        return major;
    }

    public void setClasses(List<Lecture> classes) {
        this.classes = classes;
    }

    public List<Lecture> getClasses() {
        return classes;
    }
}
