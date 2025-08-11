package NagasawaKenji.IsctClassReview.service;


import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import NagasawaKenji.IsctClassReview.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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

        return new CustomUserDetails(user);

    }


}
