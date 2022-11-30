package controllerTests;


import chatApp.SpringApp;
import chatApp.controller.ControllerUtil;
import chatApp.controller.UserController;
import chatApp.controller.entities.UserRegister;
import chatApp.entities.Response;
import chatApp.entities.User;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import chatApp.service.PermissionService;
import chatApp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringApp.class)
public class UserControllerTests {
    private static final Logger logger = LogManager.getLogger(ControllerUtil.class);
    @Autowired
    private UserController userController;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionService permissionService;
    private final String EMAIL = "chat.app3000@gmail.com";
    private final String USERNAME = "someUsername";
    private final String PASSWORD = "Password$1";
    private final String INVALID_PASSWORD = "invalidPassword";
    private final String INVALID_EMAIL = "invalidEmail";
    private ResponseEntity<String> responseEntity;
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
    public void createUser_validUserNotExistInDB_UserAddedAndResponseEntityOk(){
        deleteUserIfExists(EMAIL, USERNAME);
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, USERNAME));
        assertEquals(200, responseEntity.getStatusCodeValue(), "User input is valid and user doesn't exist in DB, but request entity didn't return ok status.");
        assertTrue(userRepository.findByEmail(EMAIL)!=null, "User input is valid and user doesn't exist in DB, but user wasn't created.");
    }

    @Test
    public void createUser_userWithInputEmailExistsInDB_returnsResponseEntityBadRequest(){
        addUserIfNotExists(EMAIL, USERNAME, PASSWORD);
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User already exists in DB, but response entity didn't return bad request.");
        assertTrue(userRepository.countByEmail(EMAIL)==1, "User with input email already exists in DB, but another user with same email was added.");
    }

    @Test
    public void createUser_userWithInputUsernameExistsInDB_returnsResponseEntityBadRequest(){
        addUserIfNotExists(EMAIL, USERNAME, PASSWORD);
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User already exists in DB, but response entity didn't return bad request.");
        assertTrue(userRepository.countByUsername(USERNAME)==1, "User with input username already exists in DB, but another user with same username was added.");
    }

    @Test
    public void createUser_userIsNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(null);
        assertEquals(responseEntity.getStatusCodeValue(),400,"User is null, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_emailIsNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(null, PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's email is null, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_emailIsEmpty_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister("", PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's email is empty, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_passwordIsNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, null, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password is null, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_passwordIsEmpty_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, "", USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password is empty, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_usernameIsNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, null));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password is null, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_usernameIsEmpty_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, ""));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password is empty, but response entity didn't return bad request.");
    }


    @Test
    public void createUser_emailInvalidFormat_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(INVALID_EMAIL, PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's email format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_passwordInvalidFormat_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, INVALID_PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password format is invalid, but response entity didn't return bad request.");
    }


    @Test
    public void activateUser_activationTokenValidUserExists_returnsResponseEntityOkUserActivated(){
        deleteUserIfExists(EMAIL,USERNAME);
        userService.addUser(EMAIL,PASSWORD,USERNAME);
        String strToEncode = EMAIL+"#"+ LocalDateTime.now().plusHours(24);
        responseEntity = userController.activateUser(Base64.getEncoder().encodeToString(strToEncode.getBytes()));

        assertEquals(responseEntity.getStatusCodeValue(),200,"User exists and token is valid, but response entity didn't return ok response.");
        assertTrue(userRepository.findByEmail(EMAIL).getUserType()==UserType.REGISTERED,"User exists and token is valid, but user wasn't activated");
    }

    /*
    @Test
    public void activateUser_activationTokenNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, INVALID_PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void activateUser_activationTokenEmpty_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, INVALID_PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void activateUser_activationTokenInvalidFormat_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, INVALID_PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void activateUser_TokenValidUserNotExists_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, INVALID_PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void activateUser_activationTokenExpiredUserExists_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, INVALID_PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password format is invalid, but response entity didn't return bad request.");
    }

     */

}
