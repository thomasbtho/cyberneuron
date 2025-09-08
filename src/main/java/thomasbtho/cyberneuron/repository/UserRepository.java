package thomasbtho.cyberneuron.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thomasbtho.cyberneuron.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByDisplayName(String displayName);
    Optional<User> findByEmail(String email);
}
