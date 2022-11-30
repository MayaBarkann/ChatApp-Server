package controllerTests;

import chatApp.SpringApp;
import chatApp.controller.UserProfileController;
import chatApp.controller.entities.UserProfileToPresent;
import chatApp.entities.User;
import chatApp.entities.UserProfile;
import chatApp.entities.UserType;
import chatApp.repository.UserProfileRepository;
import chatApp.repository.UserRepository;
import chatApp.service.UserProfileService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = SpringApp.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserProfileControllerTests {

    //private static final Logger logger = LogManager.getLogger(UserProfileControllerTests.class);
    private static List<UserProfile> userProfiles = new ArrayList<>();
    private static List<User> users = new ArrayList<>();
    @Autowired
    private UserProfileController userProfileController;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserProfileRepository userProfileRepo;
    @Autowired
    private UserRepository userRepo;

    @BeforeAll
    public void setup(){
        for (int i = 0; i < 5; i++) {
            User user = new User("UserNum" + i, "Email" + i + "@gmail.com", "Password");
            users.add(user);

        }
        //userProfiles.get(0).setPublic(true);
        users.get(0).setUserType(UserType.REGISTERED);
        users.get(1).setUserType(UserType.GUEST);
        users.get(2).setUserType(UserType.ADMIN);
        users.get(3).setUserType(UserType.REGISTERED);
        users.get(4).setUserType(UserType.REGISTERED);

        User userTemp;
        UserProfile userProfileTemp;
        for (int i = 0; i < users.size(); i++) {
            userTemp = userRepo.findByEmail(users.get(i).getEmail());

            if(userTemp == null) {
                users.set(i,userRepo.save(users.get(i)));
            } else {
                users.set(i, userTemp);
            }

            if(i == 3){
                userProfileTemp = new UserProfile(users.get(i).getId(), "lname" +i, "fname" +i, LocalDate.now(), "" + i, true,  "");
            } else {
                userProfileTemp = new UserProfile(users.get(i).getId(), "lname" +i, "fname" +i, LocalDate.now(), "" + i, false,  "");
            }

            userProfiles.add(userProfileRepo.save(userProfileTemp));

        }


    }

    @Test
    public void getUserProfileByUsername_emptyUsernameForProfileToView_BadRequest(){
        int userId = users.get(0).getId();
        ResponseEntity<UserProfileToPresent> response = userProfileController.getUserProfileByUsername(userId, "");
        Assertions.assertEquals(response.getStatusCode().value(),400,"user with empty user name should return bad request (status code 400)" );
    }

    @Test
    public void getUserProfileByUsername_nullUsernameForProfileToView_BadRequest(){
        int userId = users.get(0).getId();
        ResponseEntity<UserProfileToPresent> response = userProfileController.getUserProfileByUsername(userId, null);
        Assertions.assertEquals(response.getStatusCode().value(),400,"passing empty user name should return bad request (status code 400)" );
    }

    //todo: check if this is okay - i'm relying that nobody changed user type of users(0);
    @Test
    public void getUserProfileByUsername_userIdOfGuestUser_Unauthorized(){
        int userId = users.get(1).getId();
        String userNameOfProfileToView = users.get(0).getUsername();
        ResponseEntity<UserProfileToPresent> response = userProfileController.getUserProfileByUsername(userId, userNameOfProfileToView);
        Assertions.assertEquals(response.getStatusCode().value(),401,"guest user should not have permissions to view other user profile" );
    }

    @Test
    public void getUserProfileByUsername_userIdRegisteredUserAndExistingUsernameWithPrivateProfile_Unauthorized(){
        int userId = users.get(0).getId();
        String userNameOfProfileToView = users.get(2).getUsername();
        ResponseEntity<UserProfileToPresent> response = userProfileController.getUserProfileByUsername(userId, userNameOfProfileToView);
        Assertions.assertEquals(response.getStatusCode().value(),401,"registered user can not see other profile if the profile is private" );
    }

    @Test
    public void getUserProfileByUsername_userIdAdminUserAndExistingUsernameWithPublicProfile_Ok(){
        int userId = users.get(2).getId();
        String userNameOfProfileToViewPublic = users.get(3).getUsername();
        ResponseEntity<UserProfileToPresent> response = userProfileController.getUserProfileByUsername(userId, userNameOfProfileToViewPublic);
        Assertions.assertEquals(response.getStatusCode().value(),200,"registered user can see other public profile if exists" );
    }

    @Test
    public void getUserProfileByUsername_userIdAdminUserAndExistingRegisteredUsernameWithPrivate_Unauthorized(){
        int userId = users.get(2).getId();
        String userNameOfProfileToViewPublic = users.get(0).getUsername();
        ResponseEntity<UserProfileToPresent> response = userProfileController.getUserProfileByUsername(userId, userNameOfProfileToViewPublic);
        Assertions.assertEquals(response.getStatusCode().value(),401,"admin user can not see other private profile");
    }

    @Test
    public void getSelfUserProfileById_userIdOfGuestUser_Unauthorized(){
        int userIdGuest = users.get(1).getId();
        ResponseEntity<UserProfileToPresent> response = userProfileController.getSelfUserProfileById(userIdGuest);
        Assertions.assertEquals(response.getStatusCode().value(),401,"guest user does not have profile");
    }

    @Test
    public void getSelfUserProfileById_userIdOfAdminUser_Ok(){
        int userIdAdmin = users.get(2).getId();
        ResponseEntity<UserProfileToPresent> response = userProfileController.getSelfUserProfileById(userIdAdmin);
        Assertions.assertEquals(response.getStatusCode().value(),200,"admin user can get his own profile");
    }

    @Test
    public void getSelfUserProfileById_userIdOfRegisterUser_Ok(){
        int userIdRegister = users.get(3).getId();
        ResponseEntity<UserProfileToPresent> response = userProfileController.getSelfUserProfileById(userIdRegister);
        Assertions.assertEquals(response.getStatusCode().value(),200,"admin user can get his own profile");
    }

    //todo: go back and check again about setting local date as now
    @Test
    public void editUserProfile_userIdOfRegisteredUser_Ok(){
        User registeredUser = users.get(3);
        UserProfileToPresent userProfileToPresent = UserProfileToPresent.createFromUserProfileAndUser(userProfiles.get(3), registeredUser);
        userProfileToPresent.setDateOfBirth(LocalDate.now());
        ResponseEntity<UserProfileToPresent> response = userProfileController.editUserProfile(registeredUser.getId(), userProfileToPresent);
        Assertions.assertEquals(response.getBody(),userProfileToPresent,"registered user can edit his profile");
    }









}
