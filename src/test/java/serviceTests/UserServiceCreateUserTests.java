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
public class UserServiceCreateUserTests {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private final String email = "some.email@gmail.com";
    private final String password = "hfghF$ff123";
    private final String username = "someUsername";
    private User user;

    @Before
    public void initEach(){
        user = userRepository.findByEmail(email);
        if(user!=null) {
            userRepository.deleteById(user.getId());
        }
        userService.addUser(email,password,username);
        user = userRepository.findByEmail(email);
    }


    @Test
    public void addNewUser_userNotExistInDBInputCorrect_userAddedToDB(){
        assertNotNull(user, "New user wasn't added to DB,even though such user doesn't exist in DB and all input is correct");
    }


    @Test
    public void addNewUser_userNotExistInDB_addedUserEmailIsCorrect(){
        assertEquals(email, user.getEmail(), "Inputted user email and the email saved in user table in DB don't match.");
    }

    @Test
    public void addNewUser_userNotExistInDB_addedUserPasswordIsCorrect(){
        assertTrue(ServiceUtil.isPasswordCorrect(user.getPassword(),password), "Inputted user's password and decrypted password from user table in DB don't match.");
    }

    @Test
    public void addNewUser_userNotExistInDB_userAddedUsernameCorrect(){
        assertEquals(this.username, user.getUsername(), "Inputted user's username and username saved in table user in DB don't match.");
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
        userService.addUser(email,"gggjFG5$1","anotherUser");
        long count = userRepository.countByEmail(email);
        User checkUser = userRepository.findByEmail(email);
        assertTrue(count==1 && checkUser.getId()== user.getId(), "User with same email exists but a new User with the same email was added.");

    }

    @Test
    public void addUser_userWithSameUsernameExistsInDB_newUserNotAddedToDB(){
        userService.addUser("daadd@gmail.com","gggjFG5$1",this.username);
        long count = userRepository.countByUsername(this.username);
        System.out.println(count);

        User checkUser = userRepository.findByUsername(username);
        System.out.println(checkUser.getId() + "," +user.getId());
        assertTrue(count==1 && checkUser.getId()==user.getId(), "User with same username exists but a new User with the same username was added.");
    }

    @Test
    public void addUser_userAlreadyExistsInDB_existingUserNotChanged(){
        userService.addUser(email,"gggjFG5$1","fdfds");
        User checkUser = userRepository.findByEmail(email);
        assertEquals(checkUser, user, "User already exists but adding user with same email changed his field values.");
    }

}
