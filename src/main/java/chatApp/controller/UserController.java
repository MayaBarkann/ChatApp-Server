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
     *
     * @param user
     * @return ResponseEntity<String>, will hold: if action succeeded - saved user data; if action failed:reason for failure
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody User user) {
        Response response = userService.addUser(user);
        if (response.isSucceed()) {
            emailActivationService.sendActivationMail(user.getEmail());
            return ResponseEntity.ok("User added successfully: " + UserToPresent.createFromUser(user).toString());
        }
        return ResponseEntity.badRequest().body(response.getMessage());
    }

}
