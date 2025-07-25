package NagasawaKenji.IsctClassReview.repository;

import NagasawaKenji.IsctClassReview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Short> {

    public Optional<User> findByUserName(String userName);
}
