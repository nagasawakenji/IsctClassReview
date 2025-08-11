package NagasawaKenji.IsctClassReview.service;

import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.exception.NotFoundException;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import NagasawaKenji.IsctClassReview.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;

@Service
public class UserSettingService {

    private final UserRepository userRepo;

    @Autowired
    public UserSettingService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public void changeDisplayName(String name,
                                  CustomUserDetails principal){
       if (name == null || name.isBlank()) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "表示名を入力してください");
       }

       User user = userRepo.findById(principal.getUser().getId())
               .orElseThrow(() -> new NotFoundException("user not found"));

       user.setDisplayName(name);

        CustomUserDetails updated = new CustomUserDetails(user);
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updated,
                current.getCredentials(),
                updated.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

    }
}
