package NagasawaKenji.IsctClassReview.dto;

import java.time.LocalDateTime;

public class UserDTO {

    private final String userName;
    private final String password;
    private final String displayName;
    private final String email;
    private final LocalDateTime createdAt;

    public UserDTO(String userName, String password,
                   String displayName, String email,
                   LocalDateTime createdAt) {
        this.userName = userName;
        this.password = password;
        this.displayName = displayName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
