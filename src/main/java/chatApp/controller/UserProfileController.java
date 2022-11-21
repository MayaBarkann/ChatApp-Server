package chatApp.controller;

import chatApp.Entities.Response;
import chatApp.Entities.UserActions;
import chatApp.Entities.UserProfile;
import chatApp.controller.entities.UserProfileToPresent;
import chatApp.service.PermissionService;
import chatApp.service.UserProfileService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("/edit")
    public Response<UserProfile> editUserProfile(@RequestBody UserProfile userProfile, @RequestParam("path") String localImagePath){
        if(userProfile == null){
            return Response.createFailureResponse("Can not update user profile - user does not exists");
        }
        Response<Boolean> response = permissionService.checkPermission(userProfile.getId(), UserActions.HasProfile);
        if (!response.isSucceed()){
            return Response.createFailureResponse("Can not update user profile - this user does not have permissions for editing profile");
        }
        return userProfileService.editUserProfile(userProfile, localImagePath);
    }

    /***
     * load user profile by user id. The method checks if the user exists and has profile by calling to permission manager.
     * @param userId
     * @return response with the user profile if it has the permissions for it, if not return failure response with the right message
     */
    @GetMapping("/load")
    public Response<UserProfileToPresent> getUserProfileById(@RequestParam("id") int userId){

        Response<Boolean> response = permissionService.checkPermission(userId, UserActions.HasProfile);
        if (!response.isSucceed()){
            return Response.createFailureResponse("Could not load user profile - this user does not have the permissions for it");

        } else {
            Response<UserProfile> responseProfile = userProfileService.getUserProfileById(userId);
            Response<User> responseUser = userService.findUserById(userId);

            if(!responseProfile.isSucceed() || !responseUser.isSucceed()){
                return Response.createFailureResponse("could not load user profile");
            }

            return Response.createSuccessfulResponse(UserProfileToPresent.createFromUserProfileAndUser(responseProfile.getData(),
                    responseUser.getData()));
        }

    }



}
