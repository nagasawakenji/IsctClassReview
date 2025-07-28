package NagasawaKenji.IsctClassReview.service;

import NagasawaKenji.IsctClassReview.entity.EmailVerificationToken;
import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.exception.NotFoundException;
import NagasawaKenji.IsctClassReview.repository.EmailVerificationTokenRepository;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTests {

    private static final String EMAIL    = "test@m.titech.ac.jp";
    private static final String RAW_PASS = "testPassword";
    private static final String TOKEN    = "111111";

    @Mock EmailVerificationTokenRepository tokenRepo;
    @Mock UserRepository                userRepo;
    @Mock JavaMailSender                mailSender;
    @Mock PasswordEncoder               passwordEncoder;

    RegistrationService service;

    @BeforeEach
    void setUp() {
        service = new RegistrationService(
                tokenRepo, userRepo, mailSender, passwordEncoder
        );
    }

    @Test
    @DisplayName("sendVerificationCode: 6桁トークンを保存し、メール送信")
    void sendVerificationCode_success() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        service.sendVerificationCode(EMAIL);

        verify(tokenRepo).deleteByEmail(EMAIL);

        ArgumentCaptor<EmailVerificationToken> capToken =
            ArgumentCaptor.forClass(EmailVerificationToken.class);
        verify(tokenRepo).save(capToken.capture());
        EmailVerificationToken saved = capToken.getValue();
        assertThat(saved.getEmail()).isEqualTo(EMAIL);
        assertThat(saved.getToken()).matches("\\d{6}");
        assertThat(saved.getExpiresAt())
            .isAfter(before.plusMinutes(14))
            .isBefore(before.plusMinutes(16));

        ArgumentCaptor<SimpleMailMessage> capMsg =
            ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(capMsg.capture());
        SimpleMailMessage msg = capMsg.getValue();
        assertThat(msg.getTo()).containsExactly(EMAIL);
        assertThat(msg.getSubject())
            .isEqualTo("【科学大OCW非公認シラバスシステム】認証コードのお知らせ");
        assertThat(msg.getText()).contains(saved.getToken());
    }

    @Nested
    @DisplayName("registerUser 異常系")
    class RegisterUserErrorTests {

        @Test
        @DisplayName("トークンが見つからなければ NotFoundException")
        void registerUser_tokenNotFound() {
            when(tokenRepo.findByEmailAndToken(EMAIL, TOKEN))
                .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> service.registerUser(EMAIL, RAW_PASS, TOKEN));
        }

        @Test
        @DisplayName("期限切れトークンは IllegalArgumentException")
        void registerUser_tokenExpired() {
            EmailVerificationToken expired =
                new EmailVerificationToken(EMAIL, TOKEN,
                    LocalDateTime.now().minusMinutes(1));
            when(tokenRepo.findByEmailAndToken(EMAIL, TOKEN))
                .thenReturn(Optional.of(expired));

            assertThrows(IllegalArgumentException.class,
                () -> service.registerUser(EMAIL, RAW_PASS, TOKEN));
        }
    }

    @Test
    @DisplayName("registerUser 正常系: ユーザー登録→トークン削除")
    void registerUser_success() {
        EmailVerificationToken valid =
            new EmailVerificationToken(EMAIL, TOKEN,
                LocalDateTime.now().plusMinutes(10));
        when(tokenRepo.findByEmailAndToken(EMAIL, TOKEN))
            .thenReturn(Optional.of(valid));
        when(passwordEncoder.encode(RAW_PASS))
            .thenReturn("encodedPw");

        service.registerUser(EMAIL, RAW_PASS, TOKEN);

        ArgumentCaptor<User> capUser =
            ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(capUser.capture());
        User savedUser = capUser.getValue();
        assertThat(savedUser.getUserName()).isEqualTo(EMAIL);
        assertThat(savedUser.getPassword()).isEqualTo("encodedPw");

        verify(tokenRepo).deleteByEmail(EMAIL);
    }
}
