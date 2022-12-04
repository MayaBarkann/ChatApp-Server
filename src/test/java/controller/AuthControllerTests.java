package controller;

import chatApp.SpringApp;
import chatApp.controller.AuthController;
import chatApp.controller.entities.LoginCredentials;
import chatApp.entities.User;
import chatApp.entities.UserStatus;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringApp.class)
public class AuthControllerTests {
    @Autowired
    private AuthController authController;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    private ResponseEntity<String> responseEntity;
    private final String EMAIL = "chat.app3000@email.com";
    private final String PASSWORD = "hfghF$ff123";
    private final String USERNAME = "someUsername";
    private final String INVALID_PASSWORD = "invalidPassword687";
    private final String INVALID_EMAIL = "invalidEmail76876";

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

    private void addRegisteredUserForTests(String email, String username, String password){
        deleteUserIfExists(email,username);
        User user = User.createNotActivatedUser(username,email,password);
        user.setUserType(UserType.REGISTERED);
        userRepository.save(user);
    }

    private User addGuestForTests(String username){
        deleteUserIfExists("",username);
        return userRepository.save(User.createGuestUser(username));
    }

    @AfterEach
    public void cleanUpEach(){
        deleteUserIfExists(EMAIL, USERNAME);
    }

    @Test
    public void userLogin_userWithInputEmailNotExistsInDB_returnsResponseEntityBadRequest(){
        deleteUserIfExists(EMAIL,"");
        responseEntity = authController.userLogin(new LoginCredentials(EMAIL, PASSWORD));
        assertEquals(responseEntity.getStatusCodeValue(),400,"No user with inputted email exists in DB, but response entity didn't return bad request.");
    }

    @Test
    public void userLogin_correctEmailInputPasswordNotMatches_returnsResponseEntityBadRequest(){
        addRegisteredUserForTests(EMAIL,USERNAME,PASSWORD);
        responseEntity = authController.userLogin(new LoginCredentials(EMAIL, "WrongPass$1"));
        User user = userRepository.findByEmail(EMAIL);
        assertEquals(responseEntity.getStatusCodeValue(),400,"The input password is incorrect, but response entity didn't return bad request.");
        assertTrue(user!=null && user.getUserStatus()!=UserStatus.ONLINE, "Input password is incorrect, but user status was set to ONLINE.");
    }

    @Test
    public void userLogin_LoginCredentialsIsNull_returnsResponseEntityBadRequest(){
        responseEntity = authController.userLogin(null);
        assertEquals(responseEntity.getStatusCodeValue(),400,"LoginCredentials object is null, but response entity didn't return bad request.");
    }

    @Test
    public void userLogin_emailIsNull_returnsResponseEntityBadRequest(){
        responseEntity = authController.userLogin(new LoginCredentials(null,PASSWORD));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's input email is null, but response entity didn't return bad request.");
    }

    @Test
    public void userLogin_emailIsEmpty_returnsResponseEntityBadRequest(){
        responseEntity = authController.userLogin(new LoginCredentials("", PASSWORD));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's input email is empty, but response entity didn't return bad request.");
    }

    @Test
    public void userLogin_passwordIsNull_returnsResponseEntityBadRequest(){
        responseEntity = authController.userLogin(new LoginCredentials(EMAIL, null));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's input password is null, but response entity didn't return bad request.");
    }

    @Test
    public void userLogin_passwordIsEmpty_returnsResponseEntityBadRequest(){
        responseEntity = authController.userLogin(new LoginCredentials(EMAIL, ""));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's input password is empty, but response entity didn't return bad request.");
    }

    @Test
    public void userLogin_emailInvalidFormat_returnsResponseEntityBadRequest(){
        deleteUserIfExists(EMAIL,USERNAME);
        responseEntity = authController.userLogin(new LoginCredentials(INVALID_EMAIL, PASSWORD));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's input email format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_passwordInvalidFormat_returnsResponseEntityBadRequest(){
        deleteUserIfExists(EMAIL,USERNAME);
        responseEntity = authController.userLogin(new LoginCredentials(EMAIL, INVALID_PASSWORD));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_userNotActivated_returnsResponseEntityBadRequest(){
        deleteUserIfExists(EMAIL,USERNAME);
        userRepository.save(User.createNotActivatedUser(USERNAME,EMAIL,PASSWORD));
        responseEntity = authController.userLogin(new LoginCredentials(EMAIL, PASSWORD));
        User user = userRepository.findByEmail(EMAIL);
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's account is not activated via email, but response entity didn't return bad request.");
        assertNotSame(user.getUserStatus(), UserStatus.ONLINE, "User's account is not activated, but user status was set to ONLINE.");
        assertFalse(authService.getTokenById(user.getId()).isPresent(), "User account wasn't activated, but user's authentication token was created.");
    }
}
