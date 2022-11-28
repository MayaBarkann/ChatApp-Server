package Tests;

import chatApp.SpringApp;
import chatApp.controller.UserController;
import chatApp.controller.entities.UserRegister;
import chatApp.repository.UserRepository;
import chatApp.service.UserService;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringApp.class)
@EnableAspectJAutoProxy
@Aspect
public class UserControllerTests {
    @Autowired
    private UserController userController;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepo;


    @Test

    public void testValidSyntaxUserInsert() {

        //user with null email.
        UserRegister userRegisterInput = new UserRegister(null, "password", "username");
        System.out.println(userController.createUser(userRegisterInput).getStatusCode());
        Assertions.assertTrue(checkStatus(userController.createUser(userRegisterInput),400),"user with null email should return bad request.");
       //user with empty email.
        userRegisterInput.setEmail("");
        Assertions.assertTrue(checkStatus(userController.createUser(userRegisterInput),400),"user with empty email should return bad request.");
        //user with only spaces in email.
        userRegisterInput.setEmail("  ");
        Assertions.assertTrue(checkStatus(userController.createUser(userRegisterInput),400),"user with only spaces in email should return bad request.");
        //user with wrong email syntax.
        userRegisterInput.setEmail("wrongEmailSyntax");
        Assertions.assertTrue(checkStatus(userController.createUser(userRegisterInput),400),"user with wrong email syntax should return bad request.");

    }
    private  boolean checkStatus(ResponseEntity<String> responseEntity, int expectedStatus){
        return responseEntity.getStatusCode().value()== expectedStatus;
    }
}
