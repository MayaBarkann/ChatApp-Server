package controller;

import chatApp.SpringApp;
import chatApp.controller.AuthController;
import chatApp.entities.User;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

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
    private final String EMAIL = "chat.app3000@gmail.com";
    private final String PASSWORD = "Password$1";
    private final String USERNAME = "someUsername";
    private final String INVALID_PASSWORD = "invalidPassword";
    private final String INVALID_EMAIL = "invalidEmail";
    private final String INVALID_TOKEN = "invalidToken";

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

}
