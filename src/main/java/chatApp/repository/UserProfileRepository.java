package chatApp.repository;
import chatApp.Entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository {
    public interface UserRepository extends JpaRepository<UserProfile, Integer> {
    }
}
