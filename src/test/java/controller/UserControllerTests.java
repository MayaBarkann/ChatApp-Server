package controller;

import chatApp.SpringApp;
import chatApp.controller.UserController;
import chatApp.controller.entities.UserRegister;
import chatApp.entities.*;
import chatApp.repository.UserProfileRepository;
import chatApp.repository.UserRepository;
import chatApp.service.PermissionService;
import chatApp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;


import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringApp.class)
public class UserControllerTests {
    @Autowired
    private UserController userController;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository profileRepository;
    @Autowired
    private PermissionService permissionService;
    private final String EMAIL = "chat.app3000@gmail.com";
    private final String USERNAME = "someUsername";
    private final String PASSWORD = "Password$1";
    private final String INVALID_PASSWORD = "invalidPassword";
    private final String INVALID_EMAIL = "invalidEmail";
    private final String INVALID_TOKEN = "invalidToken";
    private final LocalDateTime EXPIRED_DATE = LocalDateTime.now().minusDays(2);
    private ResponseEntity<String> responseEntity;

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

    private User addAdminUserForTests(String email, String username, String password) {
        User admin = addUserForTests(email, username, password);
        admin.setUserType(UserType.ADMIN);
        return userRepository.save(admin);
    }

    private User addUserByStatusAndTypeForTests(UserStatus status,UserType type,String email, String username, String password){
        User user = addUserForTests(email, username, password);
        user.setUserStatus(status);
        user.setUserType(type);
        return userRepository.save(user);
    }

    private String createTokenForTesting(String email, LocalDateTime date){
        String strToEncode = email+"#"+ date.plusHours(24);
        return Base64.getEncoder().encodeToString(strToEncode.getBytes());
    }

    @AfterEach
    public void cleanUpEach(){
        deleteUserIfExists(EMAIL, USERNAME);
    }

    @Test
    public void createUser_validUserNotExistInDB_UserAddedAndResponseEntityOk(){
        deleteUserIfExists(EMAIL, USERNAME);
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, USERNAME));
        assertEquals(200, responseEntity.getStatusCodeValue(), "User input is valid and user doesn't exist in DB, but request entity didn't return ok status.");
        assertNotNull(userRepository.findByEmail(EMAIL), "User input is valid and user doesn't exist in DB, but user wasn't created.");
    }

    @Test
    public void createUser_userWithInputEmailExistsInDB_returnsResponseEntityBadRequest(){
        addUserForTests(EMAIL, USERNAME, PASSWORD);
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User email already exists in DB, but response entity didn't return bad request.");
        assertEquals(1, userRepository.countByEmail(EMAIL), "User with input email already exists in DB, but another user with same email was added.");
    }

    @Test
    public void createUser_userWithInputUsernameExistsInDB_returnsResponseEntityBadRequest(){
        addUserForTests(EMAIL, USERNAME, PASSWORD);
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User username already exists in DB, but response entity didn't return bad request.");
        assertEquals(1, userRepository.countByUsername(USERNAME), "User with input username already exists in DB, but another user with same username was added.");
    }

    @Test
    public void createUser_userIsNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(null);
        assertEquals(responseEntity.getStatusCodeValue(),400,"User is null, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_emailIsNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(null, PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's email is null, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_emailIsEmpty_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister("", PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's email is empty, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_passwordIsNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, null, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password is null, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_passwordIsEmpty_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, "", USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password is empty, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_usernameIsNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, null));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's username is null, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_usernameIsEmpty_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, PASSWORD, ""));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's username is empty, but response entity didn't return bad request.");
    }


    @Test
    public void createUser_emailInvalidFormat_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(INVALID_EMAIL, PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's email format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void createUser_passwordInvalidFormat_returnsResponseEntityBadRequest(){
        responseEntity = userController.createUser(new UserRegister(EMAIL, INVALID_PASSWORD, USERNAME));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's password format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void activateUser_activationTokenValidUserExistsNotActivated_returnsResponseEntityOkUserActivated(){
        addUserForTests(EMAIL,USERNAME,PASSWORD);
        responseEntity = userController.activateUser(createTokenForTesting(EMAIL,LocalDateTime.now()));
        User user = userRepository.findByEmail(EMAIL);

        assertEquals(200, responseEntity.getStatusCodeValue(), "User exists and token is valid, but response entity didn't return ok response.");
        assertSame(userRepository.findByEmail(EMAIL).getUserType(), UserType.REGISTERED, "User exists and token is valid, but user wasn't activated");
        profileRepository.deleteById(user.getId());
    }

    @Test
    public void activateUser_activationTokenNull_returnsResponseEntityBadRequest(){
        responseEntity = userController.activateUser(null);
        assertEquals(400, responseEntity.getStatusCodeValue(), "Activation token is null, but response entity didn't return bad request.");
    }

    @Test
    public void activateUser_activationTokenEmpty_returnsResponseEntityBadRequest(){
        responseEntity = userController.activateUser("");
        assertEquals(responseEntity.getStatusCodeValue(),400,"Activation token is empty, but response entity didn't return bad request.");
    }

    @Test
    public void activateUser_activationTokenInvalidFormat_returnsResponseEntityBadRequest(){
        responseEntity = userController.activateUser(INVALID_TOKEN);
        assertEquals(responseEntity.getStatusCodeValue(),400,"Activation token format is invalid, but response entity didn't return bad request.");
    }

    @Test
    public void activateUser_TokenValidUserNotExists_returnsResponseEntityBadRequest(){
        deleteUserIfExists(EMAIL,USERNAME);
        responseEntity = userController.activateUser(createTokenForTesting(EMAIL,LocalDateTime.now()));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User doesn't exist, but response entity didn't return bad request.");
    }

    @Test
    public void activateUser_activationTokenExpiredUserExists_returnsResponseEntityBadRequest(){
        addUserForTests(EMAIL,USERNAME,PASSWORD);
        responseEntity = userController.activateUser(createTokenForTesting(EMAIL,EXPIRED_DATE));
        assertEquals(responseEntity.getStatusCodeValue(),400,"Activation token expired, but response entity didn't return bad request.");
        assertNull(userRepository.findByEmail(EMAIL), "Activation token expired, but not activated user wasn't erased from DB.");
    }

    @Test
    public void activateUser_tokenValidUserAlreadyActivated_returnsResponseEntityBadRequest(){
        User user = addUserForTests(EMAIL,USERNAME,PASSWORD);
        user.setUserType(UserType.REGISTERED);
        userRepository.save(user);

        responseEntity = userController.activateUser(createTokenForTesting(EMAIL,LocalDateTime.now()));
        assertEquals(responseEntity.getStatusCodeValue(),400,"User's is already activated, but response entity didn't return bad request.");
    }

    @Test
    public void toggleMuteUnmute_existingAdminUserTogglesExistingNotAdminUser_returnsResponseEntityOk(){
        int idUserToggles = addAdminUserForTests("email@gmail.com","some_username",PASSWORD).getId();
        User userToggled = addUserForTests(EMAIL,USERNAME,PASSWORD);
        MessageAbility userMessageAbilityBeforeToggle = userToggled.getMessageAbility();

        responseEntity = userController.toggleMuteUnmute(idUserToggles,userToggled.getUsername());
        assertEquals(200, responseEntity.getStatusCodeValue(), "Existing admin user toggles message ability of existing not Admin User, but method doesn't return an Ok response.");
        assertNotSame(userMessageAbilityBeforeToggle, userRepository.findById(userToggled.getId()).get().getMessageAbility(), "Existing admin user toggles message ability of existing not Admin User, but toggled user message ability doesn't change.");
        deleteUserIfExists("email@gmail.com","some_username");
    }

    @Test
    public void toggleMuteUnmute_existingUserNoPermissionTogglesExistingUser_returnsResponseEntityUnauthorized(){
        int idUserToggles = addUserForTests("email@gmail.com","some_username",PASSWORD).getId();
        User userToggled = addUserForTests(EMAIL,USERNAME,PASSWORD);
        MessageAbility userMessageAbilityBeforeToggle = userToggled.getMessageAbility();

        responseEntity = userController.toggleMuteUnmute(idUserToggles,userToggled.getUsername());
        assertEquals(401, responseEntity.getStatusCodeValue(), "Existing user with no permission to toggle is not allowed toggle message ability of any User, but method doesn't return an unauthorized 401 response.");
        assertSame(userMessageAbilityBeforeToggle, userRepository.findById(userToggled.getId()).get().getMessageAbility(), "Existing user with no permission to toggle is not allowed toggles message ability of any User, but toggled user message ability changed.");
        deleteUserIfExists("email@gmail.com","some_username");
    }

    @Test
    public void toggleMuteUnmute_existingAdminUserTogglesExistingAdminUser_returnsResponseEntityBadRequest(){
        int idUserToggles = addAdminUserForTests("email@gmail.com","some_username",PASSWORD).getId();
        User userToggled = addAdminUserForTests(EMAIL,PASSWORD,USERNAME);
        MessageAbility userMessageAbilityBeforeToggle = userToggled.getMessageAbility();

        responseEntity = userController.toggleMuteUnmute(idUserToggles,userToggled.getUsername());
        assertEquals(401, responseEntity.getStatusCodeValue(), "Existing admin user is not allowed to toggle message ability of any admin User, but method doesn't return an unauthorized 401 response.");
        assertSame(userMessageAbilityBeforeToggle, userRepository.findById(userToggled.getId()).get().getMessageAbility(), "Existing admin user is not allowed toggles message ability of any admin User, but toggled user message ability changed");
        deleteUserIfExists("email@gmail.com","some_username");
    }

    @Test
    public void toggleMuteUnmute_existingAdminUserTogglesNotExistingUserId_returnsResponseEntityBadRequest(){
        int idUserToggles = addAdminUserForTests("email@gmail.com","some_username",PASSWORD).getId();
        User userForToggle = addUserForTests(EMAIL,USERNAME,PASSWORD);
        userRepository.deleteById(userForToggle.getId());

        System.out.println(userForToggle.getUsername());
        responseEntity = userController.toggleMuteUnmute(idUserToggles,userForToggle.getUsername());
        assertEquals(400, responseEntity.getStatusCodeValue(), "Existing admin user toggles message ability of not existing user, but method doesn't return a bad request response.");
        deleteUserIfExists("email@gmail.com","some_username");
    }

    @Test
    public void toggleMuteUnmute_notExistingAdminUserTogglesExistingUser_returnsResponseEntityBadRequest(){
        int idUserToggles = addAdminUserForTests("email@gmail.com","some_username",PASSWORD).getId();
        String usernameForToggle = addUserForTests(EMAIL,USERNAME,PASSWORD).getUsername();
        userRepository.deleteById(idUserToggles);

        responseEntity = userController.toggleMuteUnmute(idUserToggles,usernameForToggle);
        assertEquals(400, responseEntity.getStatusCodeValue(), "Existing admin user toggles message ability of not existing user, but method doesn't return a bad request response.");
        deleteUserIfExists("email@gmail.com","some_username");
    }

    @Test
    public void changeStatus_existingActivatedUserChangesStatus_returnsResponseEntityOk(){
        User user = addUserByStatusAndTypeForTests(UserStatus.AWAY,UserType.REGISTERED,EMAIL,PASSWORD,USERNAME);
        UserStatus userStatusBeforeChange = user.getUserStatus();

        responseEntity = userController.changeStatus(user.getId());
        assertEquals(200, responseEntity.getStatusCodeValue(), "Existing activated user changes his status, but method doesn't return an Ok response.");
        assertNotSame(userStatusBeforeChange, userRepository.findById(user.getId()).get().getUserStatus(), "Existing activated user changes his status, but status isn't changed.");
    }

    @Test
    public void changeStatus_existingNotActivatedUserChangesStatus_returnsResponseEntityUnauthorized(){
        User user = addUserByStatusAndTypeForTests(UserStatus.OFFLINE,UserType.NOT_ACTIVATED,EMAIL,PASSWORD,USERNAME);
        UserStatus userStatusBeforeChange = user.getUserStatus();

        responseEntity = userController.changeStatus(user.getId());
        assertEquals(401, responseEntity.getStatusCodeValue(), "Existing not activated can't changes his status, but method doesn't return an unauthorized response.");
        assertSame(userStatusBeforeChange, userRepository.findById(user.getId()).get().getUserStatus(), "Existing not activated user can't change his status, but status is changed.");
    }

    @Test
    public void changeStatus_existingOfflineUserChangesStatus_returnsResponseEntityBadUnauthorized(){
        User user = addUserByStatusAndTypeForTests(UserStatus.OFFLINE,UserType.REGISTERED,EMAIL,PASSWORD,USERNAME);
        UserStatus userStatusBeforeChange = user.getUserStatus();

        responseEntity = userController.changeStatus(user.getId());
        assertEquals(401, responseEntity.getStatusCodeValue(), "Existing offline user can't changes his status, but method doesn't return a bad request response.");
        assertSame(userStatusBeforeChange, userRepository.findById(user.getId()).get().getUserStatus(), "Existing offline user can't changes his status, but status changes.");

    }

    @Test
    public void changeStatus_ChangesStatusOfNotExistingUser_returnsResponseEntityBadRequest(){
        User user = addUserForTests(EMAIL,USERNAME,PASSWORD);
        deleteUserIfExists(EMAIL,PASSWORD);

        responseEntity = userController.changeStatus(user.getId());
        assertEquals(400, responseEntity.getStatusCodeValue(), "User id doesn't exist but attempt to change his user's status didn't return a bad request response.");
    }

}