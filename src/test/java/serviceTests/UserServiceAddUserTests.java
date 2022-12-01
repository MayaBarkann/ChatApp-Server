package serviceTests;

import chatApp.SpringApp;
import chatApp.entities.MessageAbility;
import chatApp.entities.User;
import chatApp.entities.UserStatus;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import chatApp.service.ServiceUtil;
import chatApp.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringApp.class)
public class UserServiceAddUserTests {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    private final String EMAIL = "chat.app3000@gmail.com";
    private final String PASSWORD = "hfghF$ff123";
    private final String USERNAME = "someUsername";
    private User user;

    @Before
    public void initEach(){
        user = userRepository.findByEmail(EMAIL);
        if(user==null) {
            user=userService.addUser(EMAIL, PASSWORD, USERNAME).getData();
        }
    }

    @Test
    public void addNewUser_userNotExistInDBInputCorrect_userAddedToDB(){
        assertNotNull(user, "New user wasn't added to DB,even though such user doesn't exist in DB and all input is correct");
    }


    @Test
    public void addNewUser_userNotExistInDB_addedUserEmailIsCorrect(){
        assertEquals(EMAIL, user.getEmail(), "Inputted user email and the email saved in user table in DB don't match.");
    }

    @Test
    public void addNewUser_userNotExistInDB_addedUserPasswordIsCorrect(){
        assertTrue(ServiceUtil.isPasswordCorrect(user.getPassword(), PASSWORD), "Inputted user's password and decrypted password from user table in DB don't match.");
    }

    @Test
    public void addNewUser_userNotExistInDB_userAddedUsernameCorrect(){
        assertEquals(this.USERNAME, user.getUsername(), "Inputted user's username and username saved in table user in DB don't match.");
    }

    @Test
    public void addNewUser_userNotExistInDB_userAddedWithStatusOffline(){
        assertSame(user.getUserStatus(), UserStatus.OFFLINE, "New user was added with status: " + user.getUserStatus() + ", even though user didn't login.");
    }

    @Test
    public void addNewUser_userNotExistInDB_userAddedWithTypeNotActivated(){
        assertSame(user.getUserType(), UserType.NOT_ACTIVATED, "New user was added with type: " + user.getUserType() + ", even though user didn't activate his account via email.");
    }

    @Test
    public void addNewUser_userNotExistInDB_userAddedAsUnmuted(){
        assertSame(user.getMessageAbility(), MessageAbility.UNMUTED, "New user was added as MUTED.");
    }


    @Test
    public void addUser_userWithSameEmailExistsInDB_newUserNotAddedToDB(){
        userService.addUser(EMAIL,"gggjFG5$1","anotherUser").getData();
        User checkUser = userRepository.findByEmail(EMAIL);
        long count = userRepository.countByEmail(EMAIL);
        assertTrue(count==1 && checkUser.getId()== user.getId(), "User with same email exists but a new User with the same email was added.");

    }

    @Test
    public void addUser_userWithSameUsernameExistsInDB_newUserNotAddedToDB(){
        userService.addUser("daadd@gmail.com","gggjFG5$1",USERNAME).getData();
        User checkUser = userRepository.findByEmail(EMAIL);
        long count = userRepository.countByUsername(USERNAME);
        assertTrue(count==1 && checkUser.getId()==user.getId(), "User with same username exists but a new User with the same username was added.");
    }

    @Test
    public void addUser_userAlreadyExistsInDB_existingUserNotChanged(){
        userService.addUser(EMAIL,"gggjFG5$1","fdfds").getData();
        User checkUser = userRepository.findByEmail(EMAIL);
        assertEquals(checkUser, user, "User already exists but adding user with same email changed his field values.");
    }
}
