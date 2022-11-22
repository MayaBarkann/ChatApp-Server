package chatApp.controller;

import chatApp.Entities.Response;
import chatApp.controller.entities.LoginCredentials;
import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/login" ,method = RequestMethod.POST)
    public ResponseEntity<String> userLogin(@RequestBody LoginCredentials loginCred) {
        Response<String> validateCredResponse = ControllerUtil.validateUserCredentials(loginCred.getEmail(),loginCred.getPassword());
        if(!validateCredResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during User Login. Reason: " + validateCredResponse.getMessage());

        }
        Response<String> userLoginResponse = authService.userLogin(loginCred.getEmail(), loginCred.getPassword());
        if(!userLoginResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during User Login. Reason: " + userLoginResponse.getMessage());
        }
        return ResponseEntity.ok("token: " + userLoginResponse.getData());
    }

}
