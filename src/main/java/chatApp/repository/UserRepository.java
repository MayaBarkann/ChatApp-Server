package chatApp.repository;

import chatApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface UserRepository extends JpaRepository<User, Integer> {
        User findByEmail(String email);
        User findByUsername(String username);
        long deleteByEmail(String email);
        long countByEmail(String email);
        long countByUsername(String username);
        List<User> findAllByRegisterDateTime(LocalDateTime registerDateTime);


}
