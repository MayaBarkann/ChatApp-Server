package chatApp.controller;

import chatApp.Entities.Response;
import chatApp.Entities.UserActions;
import chatApp.Entities.UserProfile;
import chatApp.controller.entities.UserProfileToPresent;
import chatApp.service.PermissionService;
import chatApp.service.UserProfileService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import chatApp.Entities.User;

@RestController
@CrossOrigin
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    /***
     * Edit user profile by users id.
     * Checks if the user has the permission to do so and if so - updates the user profile.
     * @param userProfile new user profile with the values we want to edit and the values we want to keep
     * @param localImagePath path to the image in case we want to update the image, if not then pass empty string.
     * @return response containing the new user profile
     */
    @PutMapping("/edit")
    public ResponseEntity<String> editUserProfile(@RequestBody UserProfile userProfile, @RequestParam("path") String localImagePath){
        if(userProfile == null){
            return ResponseEntity.badRequest().body("user not found.");
        }
        Response<Boolean> response = permissionService.checkPermission(userProfile.getId(), UserActions.HasProfile);
        if (!response.isSucceed()){
            return ResponseEntity.badRequest().body("user not found.");

        } else if(!response.getData()){
            return ResponseEntity.status(401).body("this type of user does not have permissions to edit profile.");

        }

        Response<UserProfile> responseEdit = userProfileService.editUserProfile(userProfile, localImagePath);

        if(!responseEdit.isSucceed()){
            return ResponseEntity.status(401).body(responseEdit.getMessage());
        }

        return ResponseEntity.ok("profile was edit successfully");
    }

    /***
     * load user profile by user id. The method checks if the requesting user exists and has permissions to view profile
     * and checks if the profile we want to view exists and public by calling to permission manager.
     * @param userId - the user id of the user requesting to view profile
     * @param userIdToView - the id of the profile we want to view
     * @return response with the user profile if it has the permissions for it, if not return failure response with the right message
     */
    @GetMapping("/load")
    public ResponseEntity<UserProfileToPresent> getUserProfileById(@RequestParam("id") int userId, @RequestParam("id_of_user_profile_to_view") int userIdToView){
        Response<Boolean> responseRequestUserHasPermissionsToViewProfile = permissionService.checkPermission(userId, UserActions.ViewProfile);
        Response<Boolean> responseUserToViewHasProfile = permissionService.checkPermission(userId, UserActions.HasProfile);

        if(responseRequestUserHasPermissionsToViewProfile.isSucceed() && responseUserToViewHasProfile.isSucceed()){

            if(responseRequestUserHasPermissionsToViewProfile.getData() && responseUserToViewHasProfile.getData()){
                Response<UserProfile> responseViewProfile = userProfileService.getUserProfileById(userIdToView);
                Response<User> responseViewUser = userService.findUserById(userIdToView);

                if(responseViewProfile.getData().isPublic() || userId == userIdToView){

                    return ResponseEntity.ok(UserProfileToPresent.createFromUserProfileAndUser(responseViewProfile.getData(),
                            responseViewUser.getData()));
                }
            }

            return ResponseEntity.status(401).body(null);
        }

        return ResponseEntity.badRequest().body(null);
    }
}
