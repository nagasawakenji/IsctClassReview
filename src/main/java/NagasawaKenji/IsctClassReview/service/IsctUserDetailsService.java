package NagasawaKenji.IsctClassReview.service;


import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class IsctUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Autowired
    public IsctUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String userName)
            throws UsernameNotFoundException
    {
        User user = userRepo.findByUserName(userName)
                .orElseThrow(() ->
                        new UsernameNotFoundException("ユーザーが見つかりません: " + userName)
                        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .roles("STUDENT")
                .build();

    }


}
