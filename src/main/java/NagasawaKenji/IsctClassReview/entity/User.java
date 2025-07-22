package NagasawaKenji.IsctClassReview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "username", length = 50, nullable = false)
    private String userName;

    @NotNull
    @Size(max = 255)
    @Column(length = 255, nullable = false)
    private String password;

    @NotNull
    @Size(max = 100)
    @Column(name = "display_name", length = 100, nullable = false)
    private String displayName;

    @NotNull
    @Email
    @Size(max = 255)
    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User(String userName, String password,
                String displayName, LocalDateTime createdAt,
                String email) {
        this.userName = userName;
        this.password = password;
        this.displayName = displayName;
        this.createdAt = createdAt;
        this.email = email;

    }

    public User() {};

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
