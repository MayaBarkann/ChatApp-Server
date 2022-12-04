package chatApp.controller;

import chatApp.entities.Response;
import chatApp.entities.User;
import chatApp.controller.entities.UserRegister;
import chatApp.entities.UserActions;
import chatApp.controller.entities.UserToPresent;
import chatApp.service.PermissionService;
import chatApp.service.UserActivationService;
import chatApp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserActivationService userActivationService;
    @Autowired
    private PermissionService permissionService;

    private static final Logger logger = LogManager.getLogger(UserController.class);

    /**
     * Activates the addUser() method of UserService, if successful activates sendActivationEmail() method of UserActivationService.
     *
     * @param userRegister details of user inputted during registration and sent with the request.
     * @return ResponseEntity<String>, will hold: if action succeeded - saved user data; if action failed:reason for failure
     */
    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public ResponseEntity<String> createUser(@RequestBody UserRegister userRegister) {
        logger.trace("UserController createUser method start. RequestMethod.POST, path value: /register.");
        logger.debug("param ReguestBody userRegister = " + userRegister);
        if (userRegister == null) {
            logger.warn("userRegister request body param is null.");
            logger.debug("createUser returns ResponseEntity.badRequest(), reason: RegisteredUser is null.");
            return ResponseEntity.badRequest().body("Error during user register. Reason: " + "registered user can't be null.");
        }
        logger.trace("createUser method: Checking if userRegister email and password are in valid format.");
        Response<String> isValidResponse = ControllerUtil.validateUserCredentials(userRegister.getEmail(), userRegister.getPassword());
        if (!isValidResponse.isSucceed()) {
            logger.debug("createUser returns ResponseEntity.badRequest(), reason: ." + isValidResponse.getMessage());
            return ResponseEntity.badRequest().body("Error during user register. Reason: " + isValidResponse.getMessage());
        }
        logger.trace("createUser method: Checking if userRegister username is in valid format.");
        isValidResponse = ControllerUtil.isUsernameValid(userRegister.getUsername());
        if (!isValidResponse.isSucceed()) {
            logger.debug("createUser returns ResponseEntity.badRequest(), reason: ." + isValidResponse.getMessage());
            return ResponseEntity.badRequest().body("Error during user register. Reason: " + isValidResponse.getMessage());
        }
        logger.trace("createUser method: Attempting to add user to DB.");
        Response<User> responseAddUser = userService.addUser(userRegister.getEmail(), userRegister.getPassword(), userRegister.getUsername());
        if (responseAddUser.isSucceed()) {
            Response<String> responseEmail = userActivationService.sendActivationEmail(userRegister.getEmail());
            if (!responseEmail.isSucceed()) {
                logger.error("User added to DB, but error occurred while trying to send activation email: " + responseEmail.getMessage());
                logger.warn(userRegister + " deleted from DB because error occurred while trying to send activation email.");
                userService.deleteUserByEmail(userRegister.getEmail());
                return ResponseEntity.badRequest().body("Problem sending activation email to address: " + responseEmail.getMessage());
            }
            logger.debug("createUser returns ResponseEntity.ok");
            return ResponseEntity.ok("User added successfully: " + UserToPresent.createFromUser(responseAddUser.getData()).toString() + "\nActivation email was sent to: " + userRegister.getEmail());
        }
        logger.debug("createUser returns ResponseEntity.badRequest(), reason: ." + responseAddUser.getMessage());
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
        logger.trace("UserController activateUser method start. RequestMethod.GET, path value: /activate/{activationToken}.");
        logger.debug("param PathVariable activationToken = " + activationToken);
        if (activationToken == null || activationToken == "") {
            logger.warn("method param activationToken is null or empty.");
            return ResponseEntity.badRequest().body("Activation token can't be null or empty.");
        }
        logger.trace("activateUser: Attempting to activate user account.");
        Response<String> response = userActivationService.activateUser(activationToken);
        if (response.isSucceed()) {
            logger.debug("activateUser returns ResponseEntity.ok");
            return ResponseEntity.ok("Account with email" + response.getData() + " was activated successfully.");
        }
        logger.debug("activateUser returns ResponseEntity.badRequest(), reason: ." + response.getMessage());
        return ResponseEntity.badRequest().body(response.getMessage());
    }

    /**
     * Toggles the message ability if the users have the right permissions to do it.
     *
     * @param userIdPerformsTheToggle - id of user who wants to toggle another user
     * @param userIdToToggle          - id of the user we perform the toggle on
     * @return Successful response if the toggle succeeded otherwise - failure response with the reason
     */
    @PutMapping("/auth/toggle-mute-unmute")
    public ResponseEntity<String> toggleMuteUnmute(@RequestAttribute("userId") int userIdPerformsTheToggle, @RequestParam("userIdToToggle") int userIdToToggle) {
        logger.trace("UserController toggleMuteUnmute method start. RequestMethod.PUT, path value: /auth/toggle-mute-unmute.");
        logger.debug("param RequestAttribute(\"userId\") int userIdPerformsTheToggle = " + userIdPerformsTheToggle);
        logger.debug("param @RequestParam(\"userIdToToggle\") int userIdToToggle = " + userIdToToggle);

        Response<Boolean> responseHasPermissionsToToggle = permissionService.checkPermission(userIdPerformsTheToggle, UserActions.MuteOrUnmuteOthers);
        Response<Boolean> responseUserExistsAndCanBeToggledByOthers = permissionService.checkPermission(userIdToToggle, UserActions.MuteOrUnmuteOthers);

        logger.trace("toggleMuteUnmute method: Checking if user's message ability can be toggled.");
        if (responseHasPermissionsToToggle.isSucceed() && responseUserExistsAndCanBeToggledByOthers.isSucceed()) {
            if (responseHasPermissionsToToggle.getData() && !responseUserExistsAndCanBeToggledByOthers.getData()) {
                logger.trace("toggleMuteUnmute method: Attempting to toggle user's message ability.");
                Response<User> responsOfToggleUser = userService.toggleMessageAbility(userIdToToggle);
                if (responsOfToggleUser.isSucceed()) {
                    logger.debug("toggleMuteUnmute returns ResponseEntity.ok");
                    return ResponseEntity.ok("user ability to send messages has changed successfully");
                }
            }
            logger.debug("toggleMuteUnmute returns ResponseEntity.status(401) Unauthorized. Reason: user with id: " + userIdPerformsTheToggle + " is not allowed to change message ability of users.");
            return ResponseEntity.status(401).body("can not perform action- user does not have the right permissions to do it");
        }
        logger.debug("activateUser returns ResponseEntity.badRequest(), reason: ." + responseHasPermissionsToToggle.getMessage() + " " + responseUserExistsAndCanBeToggledByOthers.getMessage());
        return ResponseEntity.badRequest().body(responseHasPermissionsToToggle.getMessage() + " " + responseUserExistsAndCanBeToggledByOthers.getMessage());
    }

    /**
     * method for changing the user status. If the user status is online - the method changes the status to away
     * and the other way around
     *
     * @param userId
     * @return response entity
     */
    @PutMapping("/auth/change-status")
    public ResponseEntity<String> changeStatus(@RequestAttribute("userId") int userId) {
        //todo: add a check (at filter layer) that checks that the user is connected
        logger.trace("UserController changeStatus method start. RequestMethod.PUT, path value: /auth/toggle-mute-unmute.");
        logger.debug("RequestAttribute(\"userId\") int userId = " + userId);

        Response<Boolean> hasPermissionsToChangeStatus = permissionService.checkPermission(userId, UserActions.ChangeStatus);
        logger.trace("changeStatus method: checking if user has permission to change his status.");
        if (hasPermissionsToChangeStatus.isSucceed()) {
            if (hasPermissionsToChangeStatus.getData()) {
                logger.trace("changeStatus method: attempting to change user status.");
                Response<User> changeStatus = userService.changeStatus(userId);
                if (changeStatus.isSucceed()) {
                    logger.trace("changeStatus returns ResponseEntity.ok.");
                    return ResponseEntity.ok("user status changed successfully to- " + changeStatus.getData().getUserStatus());
                }
                logger.debug("changeStatus returns ResponseEntity.status(401) Unauthorized. Reason: " + changeStatus.getMessage());
                return ResponseEntity.status(401).body(changeStatus.getMessage());
            }
            logger.debug("changeStatus returns ResponseEntity.status(401) Unauthorized. Reason: user with id " + userId + "can't change his value.");
            return ResponseEntity.status(401).body("can not change status, this user does not have permissions");
        }
        logger.debug("activateUser returns ResponseEntity.badRequest(), reason: user with id: " + userId + "doesn't exist in DB.");
        return ResponseEntity.badRequest().body("can not change user status - this user does not exist");
    }

    /**
     * Finds and returns all the registered and admin users.
     *
     * @param userId,  int id of user that requests the list.
     * @param fromDate - optional parameter, if not given - the method returns all registered users.
     *                 if given - the method returns all registered users that registered after the given date.
     * @return ResponseEntity<List < UserToPresent>>, Response entity containing the list of all registered and admin users with the data that can be shown.
     */
    @GetMapping("/auth/get-registered-users")
    public ResponseEntity<List<UserToPresent>> getAllRegisteredUsers(@RequestAttribute("userId") int userId, @RequestParam(required = false) String fromDate) {
        logger.trace("UserController getAllRegisteredUsers method start. RequestMethod.GET, path value: /auth/get-all-registered-users.");
        logger.debug("RequestAttribute(\"userId\") int userId = " + userId);
        Response<Boolean> hasPermissionsToGetAllUsers = permissionService.checkPermission(userId, UserActions.GetAllUsers);
        logger.trace("getAllRegisteredUsers method: checking if user has permission for this action.");
        if (hasPermissionsToGetAllUsers.isSucceed()) {
            if (hasPermissionsToGetAllUsers.getData()) {
                logger.trace("getAllRegisteredUsers method: attempting to retrieve the list.");
                List<UserToPresent> listOfRegisteredUsers =
                        userService.getRegisteredUser(ControllerUtil.convertOffsetToLocalDateTime(fromDate)).stream().map(UserToPresent::createFromUser).collect(Collectors.toList());
                logger.trace("getAllRegisteredUsers returns ResponseEntity.ok.");
                return ResponseEntity.ok(listOfRegisteredUsers);
            }
            logger.debug("getAllRegisteredUsers returns ResponseEntity.status(401) Unauthorized.");
            return ResponseEntity.status(401).body(null);
        }
        logger.debug("getAllRegisteredUsers returns ResponseEntity.status(401) Unauthorized.");
        return ResponseEntity.badRequest().body(null);
    }
    @GetMapping("/auth/name")
    public ResponseEntity<String> getUserName(@RequestAttribute("userId") int userId) {
        logger.trace("UserController getUserName method start. RequestMethod.GET, path value: /auth/get-user-name.");
        String userName = userService.getUserNameById(userId);
        logger.info(String.format("getUserName(%d) returns ResponseEntity.ok.",userId));
        return ResponseEntity.ok(userName);
    }
}
