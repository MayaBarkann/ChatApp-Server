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
import org.springframework.dao.EmptyResultDataAccessException;
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

    private final String EMAIL="some.email@gmail.com";

    private final String USERNAME ="someUsername";

    private final String PASSWORD ="Password$1";

    private final int ID = 111;

    private Response<String> response;

    private Response<User> userResponse;

    private void deleteUserIfExists(String email,String username){
        User user = userRepository.findByEmail(email);
        if(user!=null) {
            userRepository.deleteById(user.getId());
        }
        user = userRepository.findByUsername(username);
        if(user!=null) {
            userRepository.deleteById(user.getId());
        }
    }

    private void addUserIfNotExists(String email,String username,String password){
        if(userRepository.findByEmail(email)==null && userRepository.findByUsername(username)==null){
            userRepository.save(new User(username,email,password));
        }
    }

    @Test
    public void validateUserInput_emailIsNull_returnsFailureResponse(){
        response = userService.validateUserInput(null, USERNAME);
        assertTrue(!response.isSucceed() && response.getData()==null, "Email is null, but method didn't return failure response.");
    }

    @Test
    public void validateUserInput_emailIsEmpty_returnsFailureResponse(){
        response = userService.validateUserInput("", USERNAME);
        assertTrue(!response.isSucceed() && response.getData()==null, "Email is empty, but method didn't return failure response.");
    }


    @Test
    public void validateUserInput_usernameIsNull_returnsFailureResponse(){
        response = userService.validateUserInput(EMAIL,null);
        assertTrue(!response.isSucceed() && response.getData()==null, "Username is null, but method didn't return failure response.");
    }


    @Test
    public void validateUserInput_usernameIsEmpty_returnsFailureResponse(){
        response = userService.validateUserInput(EMAIL,"");
        assertTrue(!response.isSucceed() && response.getData()==null, "Username is empty, but method didn't return failure response.");
    }

    @Test
    public void validateUserInput_emailIsTaken_returnsFailureResponse(){
        addUserIfNotExists(EMAIL, USERNAME, PASSWORD);
        response = userService.validateUserInput(EMAIL, USERNAME);
        assertTrue(!response.isSucceed() && response.getData()==null, "Email is taken, but method didn't return failure response.");
    }

    @Test
    public void validateUserInput_usernameIsTaken_returnsFailureResponse(){
        addUserIfNotExists(EMAIL, USERNAME, PASSWORD);
        response = userService.validateUserInput(EMAIL, USERNAME);
        assertTrue(!response.isSucceed() && response.getData()==null, "email is null, but method didn't return failure response.");
    }

    @Test
    public void validateUserInput_notEmptyNotNullUsernameEmailAreFree_returnsSuccessfulResponse(){
        deleteUserIfExists(EMAIL, USERNAME);
        response = userService.validateUserInput(EMAIL, USERNAME);
        assertTrue(response.isSucceed() && response.getData()!=null, "Email and username are free, but method didn't return a successful response.");
    }

    @Test
    public void toggleMessageAbility_userExistsUnmuted_userSetToMutedReturnsSuccessfulResponse(){
        addUserIfNotExists(EMAIL, USERNAME, PASSWORD);
        User user = userRepository.findByEmail(EMAIL);
        user.setMessageAbility(MessageAbility.UNMUTED);
        userRepository.save(user);

        userResponse = userService.toggleMessageAbility(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getMessageAbility(), MessageAbility.MUTED, "User was unmuted but toggleMessageAbility didn't change user to muted.");
        assertTrue(userResponse.isSucceed(), "User message ability was toggled but method didn't return a successful response.");
    }


    @Test
    public void toggleMessageAbility_userExistsMuted_userSetToUnmutedReturnsSuccessfulResponse(){
        addUserIfNotExists(EMAIL, PASSWORD, USERNAME);
        User user = userRepository.findByEmail(EMAIL);
        user.setMessageAbility(MessageAbility.MUTED);
        userRepository.save(user);

        userResponse = userService.toggleMessageAbility(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getMessageAbility(), MessageAbility.UNMUTED, "User was muted but toggleMessageAbility didn't change user to unmuted.");
        assertTrue(userResponse.isSucceed(), "User message ability was toggled but method didn't return a successful response.");
    }

    @Test
    public void toggleMessageAbility_userNotExists_returnsFailureResponse(){
        if(userRepository.findById(ID).isPresent()) {
            userRepository.deleteById(ID);
        }

        userResponse = userService.toggleMessageAbility(ID);
        assertTrue(!userResponse.isSucceed(), "User doesn't exist but toggleMessageAbility() didn't return failure response");
    }

    @Test
    public void changeStatus_userExistsStatusOnline_userStatusSetToAway(){
        addUserIfNotExists(EMAIL, PASSWORD, USERNAME);
        User user = userRepository.findByEmail(EMAIL);
        user.setUserStatus(UserStatus.ONLINE);
        userRepository.save(user);

        userResponse = userService.changeStatus(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getUserStatus(), UserStatus.AWAY, "User status is online but statusChange action didn't change user status to away.");
        assertTrue(userResponse.isSucceed(), "User status was changed but method didn't return a successful response.");
    }

    @Test
    public void changeStatus_userExistsStatusAway_userStatusOnline(){
        addUserIfNotExists(EMAIL, PASSWORD, USERNAME);
        User user = userRepository.findByEmail(EMAIL);
        user.setUserStatus(UserStatus.AWAY);
        userRepository.save(user);

        userResponse = userService.changeStatus(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getUserStatus(), UserStatus.ONLINE, "User status is away but statusChange action didn't change user status to online.");
        assertTrue(userResponse.isSucceed(), "User status was changed but method didn't return a successful response.");
    }

    @Test
    public void changeStatus_userExistsStatusOffline_returnsFailureResponse(){
        addUserIfNotExists(EMAIL, PASSWORD, USERNAME);
        User user = userRepository.findByEmail(EMAIL);
        user.setUserStatus(UserStatus.OFFLINE);
        userRepository.save(user);

        userResponse = userService.changeStatus(user.getId());
        Optional<User> checkUser = userRepository.findById(user.getId());
        assertEquals(checkUser.get().getUserStatus(), UserStatus.OFFLINE, "User status is OFFLINE but statusChange action change the status.");
        assertTrue(!userResponse.isSucceed(), "Offline User status can't be changed, but action didn't return failure response.");
    }

    @Test
    public void changeStatus_userNotExists_returnsFailureResponse(){
        if(userRepository.findById(ID).isPresent()) {
            userRepository.deleteById(ID);
        }

        userResponse = userService.changeStatus(ID);
        assertTrue(!userResponse.isSucceed(), "User doesn't exist but change status action didn't return failure response.");
    }

}
