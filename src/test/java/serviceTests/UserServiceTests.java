package serviceTests;

import chatApp.SpringApp;
import chatApp.entities.MessageAbility;
import chatApp.entities.Response;
import chatApp.entities.User;
import chatApp.entities.UserStatus;
import chatApp.repository.UserRepository;
import chatApp.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringApp.class)
public class UserServiceTests {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private String email="some.email@gmail.com";

    private String username="someUsername";

    private String password="Password$1";

    private Response<String> response;

    private Response<User> userResponse;

    private void deleteUserIfExists(String email){
        User user = userRepository.findByEmail(email);
        if(user!=null) {
            userRepository.deleteById(user.getId());
        }
    }

    @Test
    public void validateUserInput_emailIsNull_returnsFailureResponse(){
        response = userService.validateUserInput(null,username);
        assertTrue(!response.isSucceed() && response.getData()==null, "Email is null, but method didn't return failure response.");
    }

    @Test
    public void validateUserInput_emailIsEmpty_returnsFailureResponse(){
        response = userService.validateUserInput("",username);
        assertTrue(!response.isSucceed() && response.getData()==null, "Email is empty, but method didn't return failure response.");
    }


    @Test
    public void validateUserInput_usernameIsNull_returnsFailureResponse(){
        response = userService.validateUserInput(email,null);
        assertTrue(!response.isSucceed() && response.getData()==null, "Username is null, but method didn't return failure response.");
    }


    @Test
    public void validateUserInput_usernameIsEmpty_returnsFailureResponse(){
        response = userService.validateUserInput(email,"");
        assertTrue(!response.isSucceed() && response.getData()==null, "Username is empty, but method didn't return failure response.");
    }

    @Test
    public void validateUserInput_emailIsTaken_returnsFailureResponse(){
        User user = userRepository.findByUsername(username);
        if(user!=null) {
            userRepository.deleteById(user.getId());
        }
        userService.addUser(email,"Password$1",username);
        response = userService.validateUserInput(email,username);
        assertTrue(!response.isSucceed() && response.getData()==null, "Email is taken, but method didn't return failure response.");
    }

    @Test
    public void validateUserInput_usernameIsTaken_returnsFailureResponse(){
        User user = userRepository.findByEmail(email);
        if(user!=null) {
            userRepository.deleteById(user.getId());
        }
        userService.addUser(email,password,username);
        response = userService.validateUserInput(email,username);
        assertTrue(!response.isSucceed() && response.getData()==null, "email is null, but method didn't return failure response.");
    }

    @Test
    public void validateUserInput_notEmptyNotNullUsernameEmailAreFree_returnsSuccessfulResponse(){
        User user = userRepository.findByEmail(email);
        if(user!=null) {
            userRepository.deleteById(user.getId());
        }
        user = userRepository.findByUsername(username);
        if(user!=null) {
            userRepository.deleteById(user.getId());
        }
        response = userService.validateUserInput(email,username);
        assertTrue(response.isSucceed() && response.getData()!=null, "Email and username are free, but method didn't return a successful response.");
    }

    @Test
    public void toggleMessageAbility_userExistsUnmuted_userSetToMutedReturnsSuccessfulResponse(){
        userService.addUser(email,password,username);
        User user = userRepository.findByEmail(email);

        userResponse = userService.toggleMessageAbility(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getMessageAbility(), MessageAbility.MUTED, "User was unmuted but toggleMessageAbility didn't change user to muted.");
        assertTrue(userResponse.isSucceed(), "User message ability was toggled but method didn't return a successful response.");
    }


    @Test
    public void toggleMessageAbility_userExistsMuted_userSetToUnmutedReturnsSuccessfulResponse(){
        userService.addUser(email,password,username);
        User user = userRepository.findByEmail(email);
        user.setMessageAbility(MessageAbility.MUTED);
        userRepository.save(user);

        userResponse = userService.toggleMessageAbility(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getMessageAbility(), MessageAbility.UNMUTED, "User was muted but toggleMessageAbility didn't change user to unmuted.");
        assertTrue(userResponse.isSucceed(), "User message ability was toggled but method didn't return a successful response.");
    }

    @Test
    public void toggleMessageAbility_userNotExists_returnsFailureResponse(){
        userService.addUser(email,password,username);
        User user = userRepository.findByEmail(email);
        userRepository.deleteById(user.getId());

        userResponse = userService.toggleMessageAbility(user.getId());
        assertTrue(!userResponse.isSucceed(), "User doesn't exist but toggleMessageAbility() didn't return failure response");
    }

    @Test
    public void changeStatus_userExistsStatusOnline_userStatusSetToAway(){
        userService.addUser(email,password,username);
        User user = userRepository.findByEmail(email);
        user.setUserStatus(UserStatus.ONLINE);
        userRepository.save(user);

        userResponse = userService.changeStatus(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getUserStatus(), UserStatus.AWAY, "User status is online but statusChange action didn't change user status to away.");
        assertTrue(userResponse.isSucceed(), "User status was changed but method didn't return a successful response.");
    }

    @Test
    public void changeStatus_userExistsStatusAway_userStatusOnline(){
        userService.addUser(email,password,username);
        User user = userRepository.findByEmail(email);
        user.setUserStatus(UserStatus.AWAY);
        userRepository.save(user);

        userResponse = userService.changeStatus(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getUserStatus(), UserStatus.ONLINE, "User status is away but statusChange action didn't change user status to online.");
        assertTrue(userResponse.isSucceed(), "User status was changed but method didn't return a successful response.");
    }

    @Test
    public void changeStatus_userExistsStatusOffline_returnsFailureResponse(){
        userService.addUser(email,password,username);
        User user = userRepository.findByEmail(email);
        user.setUserStatus(UserStatus.OFFLINE);
        userRepository.save(user);

        userResponse = userService.changeStatus(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getUserStatus(), UserStatus.OFFLINE, "User status is OFFLINE but statusChange action change the status.");
        assertTrue(!userResponse.isSucceed(), "Offline User status can't be changed, but action didn't return failure response.");
    }

    @Test
    public void changeStatus_userNotExists_returnsFailureResponse(){
        userService.addUser(email,password,username);
        User user = userRepository.findByEmail(email);
        userRepository.deleteById(user.getId());

        userResponse = userService.changeStatus(user.getId());
        assertTrue(!userResponse.isSucceed(), "User doesn't exist but change status action didn't return failure response.");
    }
}
