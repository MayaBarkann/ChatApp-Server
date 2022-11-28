package chatApp.controller;

import chatApp.entities.Response;
import chatApp.controller.entities.LoginCredentials;
import chatApp.service.AuthService;
import chatApp.service.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO: check all methods
@RestController
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * Adds an authentication token on User login, sets user as online in database.
     *
     * @param loginCred object contains user email and password.
     * @return ResponseEntity<String> contains authentication token as String.
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
        return ResponseEntity.ok(userLoginResponse.getData());
    }

    /**
     * Adds an authentication token on Guest User login, sets guest user as online in database.
     *
     * @param guestUsername String representing guest user's username.
     * @return ResponseEntity<String> contains authentication token as String.
     */
    @RequestMapping(value = "/guestJoin" ,method = RequestMethod.POST)
    public ResponseEntity<String> guestJoin(@RequestBody String guestUsername) {
        Response<String> validateUsernameResponse = ControllerUtil.isUsernameValid(guestUsername);
        if(!validateUsernameResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during Guest User Join. Reason: " + validateUsernameResponse.getMessage());
        }
        Response<String> guestLoginResponse = authService.createGuestUser(guestUsername);
        if(!guestLoginResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during Guest User Join. Reason: " + guestLoginResponse.getMessage());
        }
        return ResponseEntity.ok("guestToken: " + guestLoginResponse.getData());
    }

    /**
     * Logs-out the user from the application.
     * Erases user's authentication token.
     * Sets Registered User as offline in database
     * Erases Guest User from database.
     *
     * @param authToken String, user's authentication token.
     * @return ResponseEntity<String>, if action succeeded - holds logged-out user's username, otherwise - error message.
     */
    @RequestMapping(value = "auth/logout" ,method = RequestMethod.GET)
    public ResponseEntity<String> userLogout(@RequestHeader("token") String authToken) {
        if(!ServiceUtil.isTokenFormatValid(authToken)){
            return ResponseEntity.badRequest().body("Error during logout. Reason: Token format is invalid.");
        }
        Response<String> logoutResponse = authService.userLogout(authToken);
        if(!logoutResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during logout. Reason: " + logoutResponse.getMessage());
        }
        return ResponseEntity.ok(logoutResponse.getData() + " successfully logged-out.");
    }

}
