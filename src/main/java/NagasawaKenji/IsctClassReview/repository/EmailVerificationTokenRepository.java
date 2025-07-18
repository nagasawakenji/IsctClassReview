package NagasawaKenji.IsctClassReview.repository;

import NagasawaKenji.IsctClassReview.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Short> {

    public Optional<EmailVerificationToken> findByEmailAndToken(String email, String token);
    public Optional<EmailVerificationToken> findByEmail(String email);
    public void deleteByEmail(String email);
}
