package NagasawaKenji.IsctClassReview.service;

import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IsctUserDetailsServiceTests {

    @Mock
    UserRepository userRepo;

    @InjectMocks
    IsctUserDetailsService service;

    @Test
    @DisplayName("DBに登録されているuserNameを受け取った場合、正しく各属性を付与する")
    void loadUserByUserName_success() {
        User mockUser = new User();
        mockUser.setUserName("テストユーザー");
        mockUser.setPassword("testPassword");

        when(userRepo.findByUserName("テストユーザー"))
                .thenReturn(Optional.of(mockUser));

        UserDetails userDetails = service.loadUserByUsername("テストユーザー");

        assertEquals("テストユーザー", userDetails.getUsername());
        assertEquals("testPassword", userDetails.getPassword());
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))

        );
    }

    @Test
    @DisplayName("DBに登録されていないuserNameを受け取った場合、エラーを返す")
    void loadByUserName_notFound() {
        when(userRepo.findByUserName("未定義ユーザー"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("未定義ユーザー"));
    }


}
