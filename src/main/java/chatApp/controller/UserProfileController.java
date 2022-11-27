package chatApp.controller;

import chatApp.entities.Response;
import chatApp.entities.UserActions;
import chatApp.entities.UserProfile;
import chatApp.controller.entities.UserProfileToPresent;
import chatApp.service.PermissionService;
import chatApp.service.UserProfileService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import chatApp.entities.User;

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
     * @param userProfileToPresent new user profile to present with the values we want to edit and the values we want to keep
     * @param localImagePath path to the image in case we want to update the image, if not then pass empty string.
     * @return response entity with status 200 if the user profile was updated successfully
     */
    @PutMapping("/edit")
    //public ResponseEntity<String> editUserProfile(@RequestBody UserProfileToPresent userProfileToPresent, @RequestParam("path") String localImagePath, @RequestParam("id") int id){
    public ResponseEntity<String> editUserProfile(@RequestBody UserProfileToPresent userProfileToPresent, @RequestParam("id") int id){

            if(userProfileToPresent == null){
            return ResponseEntity.badRequest().body("user not found.");
        }
        Response<Boolean> response = permissionService.checkPermission(id, UserActions.HasProfile);
        if (!response.isSucceed()){
            return ResponseEntity.badRequest().body("user not found.");

        } else if(!response.getData()){
            return ResponseEntity.status(401).body("this type of user does not have permissions to edit profile.");

        }

        Response<UserProfile> responseEdit = userProfileService.editUserProfile(userProfileToPresent, id);

        if(!responseEdit.isSucceed()){
            return ResponseEntity.status(401).body(responseEdit.getMessage());
        }

        return ResponseEntity.ok("profile edited successfully");
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
