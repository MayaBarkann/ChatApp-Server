package chatApp.controller;

import chatApp.Entities.Response;
import chatApp.controller.entities.LoginCredentials;
import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


//TODO: check all methods
//TODO: explain to team how to extract id from token
@Controller
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     *
     * @param loginCred
     * @return
     */
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

    /**
     *
     * @param guestUsername
     * @return
     */
    @RequestMapping(value = "/guestJoin" ,method = RequestMethod.POST)
    public ResponseEntity<String> guestJoin(@RequestBody String guestUsername) {
        Response<String> validateUsernameResponse = ControllerUtil.isUsernameValid(guestUsername);
        if(!validateUsernameResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during Guest Chat Join. Reason: " + validateUsernameResponse.getMessage());
        }
        Response<String> userLoginResponse = authService.createGuestUser(guestUsername);
        if(!userLoginResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during Guest Chat Join. Reason: " + userLoginResponse.getMessage());
        }
        return ResponseEntity.ok("guestToken: " + userLoginResponse.getData());
    }

    /**
     *
     * @param authToken
     * @return
     */
    @RequestMapping(value = "/logout" ,method = RequestMethod.GET)
    public ResponseEntity<String> userLogout(@RequestHeader("token") String authToken) {
        Response<String> userLogoutResponse = authService.guestLogout(authToken);
        if(!userLogoutResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during logout. Reason: " + userLogoutResponse.getMessage());
        }
        return ResponseEntity.ok(userLogoutResponse.getData());
    }





}
