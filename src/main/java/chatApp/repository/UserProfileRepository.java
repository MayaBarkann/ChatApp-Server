package chatApp.repository;
import chatApp.Entities.User;
import chatApp.Entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer>{
    UserProfile findById(int id);
}
