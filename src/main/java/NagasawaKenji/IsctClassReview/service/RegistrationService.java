package NagasawaKenji.IsctClassReview.service;


import NagasawaKenji.IsctClassReview.entity.EmailVerificationToken;
import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.exception.NotFoundException;
import NagasawaKenji.IsctClassReview.repository.EmailVerificationTokenRepository;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class RegistrationService {
    private final EmailVerificationTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    public RegistrationService(EmailVerificationTokenRepository tokenRepo,
                               UserRepository userRepo,
                               JavaMailSender mailSender,
                               PasswordEncoder passwordEncoder) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void sendVerificationCode(String email) {
        tokenRepo.deleteByEmail(email);

        String token = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken(email, token,
                LocalDateTime.now().plusMinutes(15));

        tokenRepo.save(emailVerificationToken);

        log.info("email_verification_tokensに仮登録情報を登録しました");

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("【科学大OCW非公認シラバスシステム】認証コードのお知らせ");
        msg.setText("あなたの認証コードは" + token + "です。15分以内に入力をして、登録を完了させてください。");
        mailSender.send(msg);

        log.info("認証コードを送信しました");
    }

    @Transactional
    public void  registerUser(String email, String rawPassword, String token) {
        EmailVerificationToken emailVerificationToken = tokenRepo.findByEmailAndToken(email, token)
                .orElseThrow(() -> {

                    log.error("email_verification_tokensに仮登録情報が存在しませんでした");

                    return new NotFoundException("認証コードが不正です");
                });
        if (emailVerificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {

            log.error("認証コードの有効期限が切れているため、登録を中止しました");

            throw new IllegalArgumentException("認証コードの有効期限が切れています");
        }

        User user = new User(email, passwordEncoder.encode(rawPassword),
                "科学大生(デフォルト)", LocalDateTime.now(),
                email);

        userRepo.save(user);

        log.info("Usersに情報を登録しました");

        tokenRepo.deleteByEmail(email);
    }



}
