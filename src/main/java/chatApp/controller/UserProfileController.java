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
@RequestMapping("auth/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    /***
     * Edit user profile by user id.
     * Checks if the user has the permission to do so and if so - updates the user profile according to the values given in userProfileToPresent.
     * @param userProfileToPresent new user profile with the values we want to edit and the values we want to keep
     * @param userId user id of the user we are editing his profile
     * @return response entity with status 200 if the user profile was updated successfully
     */
    @PutMapping("/edit")
    public ResponseEntity<UserProfileToPresent> editUserProfile(@RequestAttribute("userId") int userId , @RequestBody UserProfileToPresent userProfileToPresent){

        if(userProfileToPresent == null){
            return ResponseEntity.badRequest().body(null);
        }
        Response<Boolean> response = permissionService.checkPermission(userId, UserActions.HasProfile);
        if (!response.isSucceed()){
            return ResponseEntity.badRequest().body(null);

        } else if(!response.getData()){
            return ResponseEntity.status(401).body(null);

        }

        UserProfile oldUserProfile = userProfileService.getUserProfileById(userId).getData();
        UserProfile newUserProfile = UserProfile.createUserProfileFromIdAndUserProfileToPresent(userId, userProfileToPresent);
        Response<UserProfile> responseEdit = userProfileService.editUserProfile(newUserProfile);

        if(!responseEdit.isSucceed()){
            return ResponseEntity.status(401).body(null);
        }

        return ResponseEntity.ok(UserProfileToPresent.createFromUserProfileAndUser(responseEdit.getData(), userService.findUserById(userId).getData()));
    }

    /***
     * load user profile by user id. The method checks if the requesting user exists and has permissions to view profile
     * and checks if the profile we want to view exists and public by calling to permission manager.
     * @param userId - the user id of the user requesting to view profile
     * @param usernameToView - username of the user we want to view his profile
     * @return response with the user profile if it has the permissions for it, if not return failure response with the right message
     */
    @GetMapping("/load")
    public ResponseEntity<UserProfileToPresent> getUserProfileByUsername(@RequestAttribute("userId") int userId, @RequestParam("id_of_user_profile_to_view") String usernameToView){
        Integer userIdToView = userService.getUserIdByUserName(usernameToView);
        if (userIdToView == null){
            return ResponseEntity.badRequest().body(null);
        }
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

    /***
     * return self user profile by user id if the user has profile and the permissions to view profile
     * @param userId
     * @return response entity with the user profile to present
     */
    @GetMapping("/loadSelf")
    public ResponseEntity<UserProfileToPresent> getSelfUserProfileById(@RequestAttribute("userId") int userId){
        Response<Boolean> responseRequestUserHasPermissionsToViewProfile = permissionService.checkPermission(userId, UserActions.ViewProfile);
        Response<Boolean> responseUserToViewHasProfile = permissionService.checkPermission(userId, UserActions.HasProfile);

        if(responseRequestUserHasPermissionsToViewProfile.isSucceed() && responseUserToViewHasProfile.isSucceed()){

            if(responseRequestUserHasPermissionsToViewProfile.getData() && responseUserToViewHasProfile.getData()){
                Response<UserProfile> responseViewProfile = userProfileService.getUserProfileById(userId);
                Response<User> responseViewUser = userService.findUserById(userId);

                return ResponseEntity.ok(UserProfileToPresent.createFromUserProfileAndUser(responseViewProfile.getData(),
                        responseViewUser.getData()));
            }

            return ResponseEntity.status(401).body(null);
        }

        return ResponseEntity.badRequest().body(null);
    }


}
