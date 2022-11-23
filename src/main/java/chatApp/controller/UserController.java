package chatApp.controller;

import chatApp.Entities.Response;
import chatApp.Entities.User;
import chatApp.controller.entities.UserRegister;
import chatApp.Entities.UserActions;
import chatApp.controller.entities.UserToPresent;
import chatApp.service.PermissionService;
import chatApp.service.UserActivationService;
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
    private UserActivationService userActivationService;
    @Autowired
    private PermissionService permissionService;

    /**
     * Activates the addUser() method of UserService, if successful activates sendActivationEmail() method of UserActivationService.
     *
     * @param userRegister details of user inputted during registration and sent with the request.
     * @return ResponseEntity<String>, will hold: if action succeeded - saved user data; if action failed:reason for failure
     */
    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public ResponseEntity<String> createUser(@RequestBody UserRegister userRegister) {
        Response<String> isValidResponse = ControllerUtil.validateUserCredentials(userRegister.getEmail(),userRegister.getPassword());
        if(!isValidResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during user register. Reason: " + isValidResponse.getMessage());
        }
        isValidResponse = ControllerUtil.isUsernameValid(userRegister.getUsername());
        if(!isValidResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during user register. Reason: " + isValidResponse.getMessage());
        }
        Response<User> responseAddUser = userService.addUser(userRegister.getEmail(),userRegister.getPassword(),userRegister.getUsername());
        if (responseAddUser.isSucceed()) {
            Response<String> responseEmail = userActivationService.sendActivationEmail(userRegister.getEmail());
            if (!responseEmail.isSucceed()) {
                return ResponseEntity.badRequest().body("Problem sending activation email to address: " + responseEmail.getMessage());
            }
            return ResponseEntity.ok("User added successfully: " + UserToPresent.createFromUser(responseAddUser.getData()).toString() + "\nActivation email was sent to: " + userRegister.getEmail());
        }
        return ResponseEntity.badRequest().body(responseAddUser.getMessage());
    }

    /**
     * Activates the activateUser() method of UserActivationService.
     *
     * @param activationToken
     * @return ResponseEntity<String>, contains: if action succeeded - success message; if action failed - message with reason of failure
     */
    @RequestMapping(method = RequestMethod.GET, value = "/activate/{activationToken}")
    public ResponseEntity<String> activateUser(@PathVariable("activationToken") String activationToken) {
        Response<User> response = userActivationService.activateUser(activationToken);
        if (response.isSucceed()) {
            return ResponseEntity.ok("Account of " + response.getData().toString() + " was activated successfully.");
        }
        return ResponseEntity.badRequest().body(response.getMessage());
    }

    /**
     * Sends an activation email to the given email address, if it exists in database and user isn't already active.
     *
     * @param email String email address to where the activation email will be sent.
     * @return Response object, if action successful - contains the destination email, if action failed - contains the error message.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/resendActivationEmail")
    public ResponseEntity<String> resendActivationEmail(@RequestBody String email) {
        Response<String> validateEmailResponse = ControllerUtil.isEmailValid(email);
        if(!validateEmailResponse.isSucceed()){
            return ResponseEntity.badRequest().body("Error during resending activation email to address: " + email + ". Reason: " + validateEmailResponse.getMessage());
        }
        Response<String> responseResendEmail = userActivationService.sendActivationEmail(email);
        if (!responseResendEmail.isSucceed()) {
            return ResponseEntity.badRequest().body("Error during resending activation email to address: " + email + ". " + responseResendEmail.getMessage());
        }
        return ResponseEntity.ok("Activation email resent successfully to: " + email);
    }

    /**
     * Toggles the message ability if the users have the right permissions to do it.
     * @param userIdPerformsTheToggle - id of user who wants to toggle another user
     * @param userIdToToggle - id of the user we performs the toggle on
     * @return Successful response if the toggle succeeded otherwise- failure response with the reason
     */

    @PutMapping("/toggle-mute-unmute")
    public ResponseEntity<String> toggleMuteUnmute(@RequestParam("userIdPerformsTheToggle") int userIdPerformsTheToggle, @RequestParam("userIdToToggle") int userIdToToggle){
        Response<Boolean> responseHasPermissionsToToggle = permissionService.checkPermission(userIdPerformsTheToggle,UserActions.MuteOrUnmuteOthers);
        Response<Boolean> responseUserExistsAndCanBeToggledByOthers = permissionService.checkPermission(userIdToToggle,UserActions.MuteOrUnmuteOthers);

        if(responseHasPermissionsToToggle.isSucceed() && responseUserExistsAndCanBeToggledByOthers.isSucceed()){

            if(responseHasPermissionsToToggle.getData() && !responseUserExistsAndCanBeToggledByOthers.getData()){
                Response<User> responsOfToggleUser = userService.toggleMessageAbility(userIdToToggle);
                if(responsOfToggleUser.isSucceed()){
                    return ResponseEntity.ok("user ability to send messages has changed successfully");
                }
            }

            return ResponseEntity.status(401).body("can not perform action- user does not have the right permissions to do it");
        }

        return ResponseEntity.badRequest().body(responseHasPermissionsToToggle.getMessage() + " " + responseUserExistsAndCanBeToggledByOthers.getMessage());
    }

}
