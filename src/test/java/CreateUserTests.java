import chatApp.SpringApp;
import chatApp.controller.entities.UserRegister;
import chatApp.entities.MessageAbility;
import chatApp.entities.User;
import chatApp.entities.UserStatus;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import chatApp.service.UserService;
import javafx.application.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@ExtendWith(SpringExtension.class)

//@DataJpaTest
//@SpringBootTest
//@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/application-context.xml")
//@ContextConfiguration(classes=UserRegister.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes=SpringApp.class)
public class CreateUserTests {


    //@Mock
    @Autowired
    UserRepository userRepository;

    //@InjectMocks
    @Autowired
    UserService userService;


    private final String email = "daria.sokolov@gmail.com";
    private final String password = "hfghF$ff123";
    private final String username = "dariaso";
    private User user;

/*
    @BeforeEach
    public void initEach(){
        userRepository.deleteByEmail(email);
        userService.addUser(email,password,username);
        user = userRepository.findByEmail(email);
    }
    *
 */

    @Test
    void createNewUser_userNotExistInDBInputCorrect_userAddedToDB(){
        //userRepository.deleteByEmail(email);
        userService.addUser(email,password,username);
        user = userRepository.findByEmail(email);
        assertTrue(user!=null, "New user wasn't added to DB,even though such user doesn't exist in DB and all input is correct");
    }

    /*
    @Test
    void createNewUser_userNotExistInDB_addedUserEmailIsCorrect(){
        assertTrue(user.getEmail().equals(email), "Inputted user email and the email saved in user table in DB don't match.");
    }

    @Test
    void createNewUser_userNotExistInDB_addedUserPasswordIsCorrect(){
        assertTrue(user.getPassword().equals(password), "Inputted user's password and decrypted password from user table in DB don't match.");
    }

    @Test
    void createNewUser_userNotExistInDB_userAddedUsernameCorrect(){
        assertTrue(user.getUsername().equals(username), "Inputted user's username and username saved in table user in DB don't match.");
    }

    @Test
    void createNewUser_userNotExistInDB_userAddedWithStatusOffline(){
        assertTrue(user.getUserStatus()==UserStatus.OFFLINE, "New user was added with status: " +  user.getUserStatus() + ", even though user didn't login.");
    }

    @Test
    void createNewUser_userNotExistInDB_userAddedWithTypeNotActivated(){
        assertTrue(user.getUserType()==UserType.NOT_ACTIVATED, "New user was added with type: " + user.getUserType() + ", even though user didn't activate his account via email.");
    }

    @Test
    void createNewUser_userNotExistInDB_userAddedAsUnmuted(){
        assertTrue(user.getMessageAbility()== MessageAbility.UNMUTED, "New user was added as MUTED.");
    }

    @Test
    void createUser_userWithSameEmailExistsInDB_newUserNotAddedToDB(){
        userService.addUser(email,"gggjFG5$1","anotherUser");
        long count = userRepository.countByEmail(email);
        User checkUser = userRepository.findByEmail(email);
        assertTrue(count==1 && checkUser.getId()== user.getId(), "User with same email exists but a new User with the same email was added.");

    }

    @Test
    void addUser_userWithSameUsernameExistsInDB_newUserNotAddedToDB(){
        userService.addUser("daadd@gmail.com","gggjFG5$1",username);
        long count = userRepository.countByUsername(username);
        User checkUser = userRepository.findByUsername(username);
        assertTrue(count==1 && checkUser.getId()==user.getId(), "User with same username exists but a new User with the same username was added.");
    }

    @Test
    void createUser_userAlreadyExistsInDB_existingUserNotChanged(){
        userService.addUser(email,"gggjFG5$1","fdfds");
        User checkUser = userRepository.findByEmail(email);
        assertTrue(checkUser.equals(user), "User already exists but adding user with same email changed his field values.");
    }
*/
}
