package NagasawaKenji.IsctClassReview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class EmailVerificationToken {
    @Id
    @GeneratedValue
    private Short id;

    @Column(nullable = false)
    private String email;

    @Column(length = 6, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public EmailVerificationToken(String email, String token,
                                  LocalDateTime expiresAt) {
        this.email = email;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    protected EmailVerificationToken() {};

    public void setId(Short id) {
        this.id = id;
    }

    public Short getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
