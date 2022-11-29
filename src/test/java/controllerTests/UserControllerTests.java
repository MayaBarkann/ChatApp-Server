package controllerTests;


import chatApp.SpringApp;
import chatApp.controller.ControllerUtil;
import chatApp.controller.UserController;
import chatApp.controller.entities.UserRegister;
import chatApp.repository.UserRepository;
import chatApp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class UserControllerTests {
    public static final Logger logger = LogManager.getLogger(ControllerUtil.class);
    @Autowired
    private UserController userController;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepo;


    @Test
    public void testValidSyntaxUserInsert() {
        logger.info("testValidSyntaxUserInsert");
        logger.debug("test user with null email.");
        //user with null email.
        UserRegister userRegisterInput = new UserRegister(null, "password", "username");
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
        //user with null password.
        userRegisterInput.setPassword(null);
        Assertions.assertTrue(checkStatus(userController.createUser(userRegisterInput),400),"user with null password should return bad request.");
        //user with empty password.
        userRegisterInput.setPassword("");
        Assertions.assertTrue(checkStatus(userController.createUser(userRegisterInput),400),"user with empty password should return bad request.");
        //user with wrong password syntax.
        userRegisterInput.setPassword("wrongPasswordSyntax");
        Assertions.assertTrue(checkStatus(userController.createUser(userRegisterInput),400),"user with wrong password syntax should return bad request.");
        //user with null username.
        userRegisterInput.setUsername(null);
        Assertions.assertTrue(checkStatus(userController.createUser(userRegisterInput),400),"user with null username should return bad request.");
    }
    private  boolean checkStatus(ResponseEntity<String> responseEntity, int expectedStatus){
        int statusCode = responseEntity.getStatusCode().value();
        logger.debug("checkStatus called with responseEntity code: "+ statusCode +" and expectedStatus: "+expectedStatus);
        return statusCode == expectedStatus;
    }
}
