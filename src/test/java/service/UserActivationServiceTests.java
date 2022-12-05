package service;

import chatApp.SpringApp;
import chatApp.entities.Response;
import chatApp.entities.User;
import chatApp.entities.UserType;
import chatApp.repository.UserProfileRepository;
import chatApp.repository.UserRepository;
import chatApp.service.UserActivationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringApp.class)
public class UserActivationServiceTests {

    @Autowired
    private UserActivationService userActivationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository profileRepository;
    private Response<String> response;
    private final String EMAIL = "chat.app3000@gmail.com";
    private final String USERNAME = "someUsername";
    private final String PASSWORD = "Password$1";
    private final String INVALID_TOKEN = "invalidToken";
    private final LocalDateTime EXPIRED_DATE = LocalDateTime.now().minusDays(2);

    private String createTokenForTesting(String email, LocalDateTime date){
        String strToEncode = email+"#"+ date.plusHours(24);
        return Base64.getEncoder().encodeToString(strToEncode.getBytes());
    }

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

    private User addUserForTests(String email, String username, String password){
        deleteUserIfExists(email,username);
        return userRepository.save(User.createNotActivatedUser(username,email,password));
    }

    @AfterEach
    public void cleanUpEach(){
        deleteUserIfExists(EMAIL, USERNAME);
    }

    @Test
    public void activateUser_activationTokenValidUserExistsNotActivated_returnsSuccessfulResponse(){
        addUserForTests(EMAIL,PASSWORD,USERNAME);
        response = userActivationService.activateUser(createTokenForTesting(EMAIL,LocalDateTime.now()));
        User user = userRepository.findByEmail(EMAIL);

        assertTrue(response.isSucceed(), "User exists and token is valid, but response isn't successful.");
        assertSame(userRepository.findByEmail(EMAIL).getUserType(), UserType.REGISTERED, "User exists and token is valid, but user wasn't activated");
        profileRepository.deleteById(user.getId());
    }


    @Test
    public void activateUser_activationTokenNull_returnsFailureResponse(){
        response = userActivationService.activateUser(null);
        assertFalse(response.isSucceed(), "Activation token is null, but method didn't return failure response.");
    }

    @Test
    public void activateUser_activationTokenEmpty_returnsFailureResponse(){
        response = userActivationService.activateUser("");
        assertFalse(response.isSucceed(),"Activation token is empty, but method didn't return failure response.");
    }

    @Test
    public void activateUser_activationTokenInvalidFormat_returnsFailureResponse(){
        response = userActivationService.activateUser(INVALID_TOKEN);
        assertFalse(response.isSucceed(),"Activation token format is invalid, but method didn't return failure response.");
    }

    @Test
    public void activateUser_TokenValidUserNotExists_returnsFailureResponse(){
        deleteUserIfExists(EMAIL,"");
        response = userActivationService.activateUser(createTokenForTesting(EMAIL,LocalDateTime.now()));
        assertFalse(response.isSucceed(),"User doesn't exist, but method didn't return failure response.");
    }

    @Test
    public void activateUser_activationTokenExpiredUserExists_returnsFailureResponse(){
        addUserForTests(EMAIL,PASSWORD,USERNAME);
        response = userActivationService.activateUser(createTokenForTesting(EMAIL,EXPIRED_DATE));
        assertFalse(response.isSucceed(),"Activation token expired, but method didn't return failure response.");
        assertNull(userRepository.findByEmail(EMAIL), "Activation token expired, but not activated user wasn't erased from DB.");
    }

    @Test
    public void activateUser_tokenValidUserAlreadyActivated_returnsFailureResponse(){
        User user = addUserForTests(EMAIL,PASSWORD,USERNAME);
        user.setUserType(UserType.REGISTERED);
        userRepository.save(user);

        response = userActivationService.activateUser(createTokenForTesting(EMAIL,LocalDateTime.now()));
        assertFalse(response.isSucceed(),"User's is already activated, but response entity didn't return bad request.");
    }

    @Test
    public void sendActivationEmail_userExistsValidEmailAddress_returnsSuccessfulResponse(){
        addUserForTests(EMAIL,PASSWORD,USERNAME);
        response = userActivationService.sendActivationEmail(EMAIL);
        assertTrue(response.isSucceed(),"User exists and email is valid, but method didn't return successful response.");
    }

    @Test
    public void sendActivationEmail_invalidEmailAddressFormat_returnsFailureResponse(){
        response = userActivationService.sendActivationEmail("invalidEmail");
        assertTrue(!response.isSucceed(),"Email format is invalid, but method didn't return failure response.");
    }

    @Test
    public void sendActivationEmail_emailAddressNull_returnsFailureResponse(){
        response = userActivationService.sendActivationEmail(null);
        assertTrue(!response.isSucceed(),"Email is null, but method didn't return failure response.");
    }

    @Test
    public void sendActivationEmail_emailAddressEmpty_returnsFailureResponse(){
        response = userActivationService.sendActivationEmail("");
        assertTrue(!response.isSucceed(),"Email is empty, but method didn't return failure response.");
    }

    @Test
    public void sendActivationEmail_userEmailNotExistsInDB_returnsFailureResponse(){
        deleteUserIfExists(EMAIL,"");
        response = userActivationService.sendActivationEmail(EMAIL);
        assertTrue(!response.isSucceed(),"No user with this email exists in DB, but method didn't return failure response.");
    }

    @Test
    public void sendActivationEmail_userAlreadyActivated_returnsFailureResponse(){
        User user = addUserForTests(EMAIL,PASSWORD,USERNAME);
        user.setUserType(UserType.REGISTERED);
        userRepository.save(user);

        response = userActivationService.sendActivationEmail(EMAIL);
        assertTrue(!response.isSucceed(),"No user with this email exists in DB, but method didn't return failure response.");
    }

}
