package NagasawaKenji.IsctClassReview.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank
    @Email
    @Pattern(regexp="^[^@]+@(m\\.titech\\.ac\\.jp|m\\.isct\\.ac\\.jp)$",
             message = "@m.titech.ac.jpか@m.isct.ac.jpで終わるメールアドレスを入力してください")
    private String email;

    @NotBlank
    @Size(min=8, max=59)
    private String password;

    public RegistrationForm(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public RegistrationForm() {};

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
