package chatApp.controller;

import chatApp.Entities.Response;
import chatApp.Entities.User;
import chatApp.service.EmailActivationService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailActivationService emailActivationService;

    /**
     * Activates the addUser() method of UserService, if successful activates sendActivationEmail() method of EmailActivationService.
     *
     * @param user
     * @return ResponseEntity<String>, will hold: if action succeeded - saved user data; if action failed:reason for failure
     */
    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        Response responseUser = userService.addUser(user);
        if (responseUser.isSucceed()) {
            Response responseEmail = emailActivationService.sendActivationEmail(user.getEmail());
            if (!responseEmail.isSucceed()) {
                return ResponseEntity.badRequest().body("Problem sending activation email to address: " + responseEmail.getMessage());
            }
            return ResponseEntity.ok("User added successfully: " + UserToPresent.createFromUser(user).toString() + "\nActivation email was sent to: " + user.getEmail());
        }
        return ResponseEntity.badRequest().body(responseUser.getMessage());
    }

    /**
     * Activates the activateUser() method of EmailActivationService.
     *
     * @param activationToken
     * @return ResponseEntity<String>, contains: if action succeeded - success message; if action failed - message with reason of failure
     */
    @RequestMapping(method = RequestMethod.GET, value = "/activate/{activationToken}")
    public ResponseEntity<String> activateUser(@PathVariable("activationToken") String activationToken) {
        Response response = emailActivationService.activateUser(activationToken);
        if (response.isSucceed()) {
            return ResponseEntity.ok("Account with email " + response.getData().toString() + " was activated successfully.");
        }
        return ResponseEntity.badRequest().body(response.getMessage());
    }

}
